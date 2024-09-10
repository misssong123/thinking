package netty.wconfig.client.agentclient.processors;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import netty.wconfig.client.WConfig;
import netty.wconfig.client.WConfigCallback;
import netty.wconfig.client.WConfigClient;
import netty.wconfig.client.exceptions.WConfigClientException;
import netty.wconfig.client.utils.ValueUtil;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;

import java.util.Objects;
@Slf4j
@RequiredArgsConstructor
public class WConfigAnnotationProcessor implements BeanPostProcessor, Ordered {

    private final WConfigClient wConfigClient;


    private final Environment environment;

    // 兼容 spring 4.x
    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, @NonNull String beanName) throws BeansException {
        WConfig annotation = AnnotationUtils.findAnnotation(bean.getClass(), WConfig.class);
        if (Objects.isNull(annotation)) {
            return bean;
        }

        String namespace = ValueUtil.resolveEmbeddedValue(annotation.namespace(), environment, true);
        String localFile = ValueUtil.resolveEmbeddedValue(annotation.localFile(), environment, true);
        log.info("namespace:{}, localFile:{}", namespace, localFile);

        try {
            WConfigCallback callback = bean instanceof WConfigCallback ? (WConfigCallback) bean : null;
            if (annotation.subscribe() || Objects.nonNull(callback)) {
                wConfigClient.subscribeConfig(namespace, localFile, annotation.fileType(), bean, callback);
            } else {
                wConfigClient.getConfig(namespace, localFile, annotation.fileType(), bean);
            }
        } catch (WConfigClientException e) {
            throw new BeanInstantiationException(bean.getClass(), "", e);
        }

        return bean;
    }

    /**
     * 将 WConfigAnnotationProcessor 提到 ClientAnnotationBeanPostProcessor 之前初始化，
     * 避免在 SpringBoot 高版本中 BoundConfigurationProperties 与 SCFClientConfigurationProperties
     * 形成循环依赖。
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }

}
