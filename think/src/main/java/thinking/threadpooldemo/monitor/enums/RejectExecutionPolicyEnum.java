package thinking.threadpooldemo.monitor.enums;

import java.util.Arrays;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public enum RejectExecutionPolicyEnum {

    CallerRunsPolicy("CallerRunsPolicy"){
        @Override
        public RejectedExecutionHandler getPolicy() {
            return new ThreadPoolExecutor.CallerRunsPolicy();
        }
    },
    AbortPolicy("AbortPolicy"){
        @Override
        public RejectedExecutionHandler getPolicy() {
            return new ThreadPoolExecutor.AbortPolicy();
        }
    },
    DiscardPolicy("DiscardPolicy"){
        @Override
        public RejectedExecutionHandler getPolicy() {
            return new ThreadPoolExecutor.DiscardPolicy();
        }
    },
    DiscardOldestPolicy("DiscardOldestPolicy"){
        @Override
        public RejectedExecutionHandler getPolicy() {
            return new ThreadPoolExecutor.DiscardOldestPolicy();
        }
    };

    private final String policyName;

    RejectExecutionPolicyEnum(String policyName) {
        this.policyName = policyName;
    }

    public RejectedExecutionHandler getPolicy(){
        throw new IllegalStateException("您无法访问此方法");
    }

    /**
     * 如果未找到对应的拒绝策略，则返回CallerRunsPolicy
     * @param policyName 策略名称
     * @return 拒绝策略
     */
    public static RejectExecutionPolicyEnum getPolicyEnum(String policyName){
        return Arrays.stream(values()).filter(input -> input.policyName.equals(policyName)).findAny().orElse(CallerRunsPolicy);
    }
}
