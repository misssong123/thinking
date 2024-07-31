package thinking.asm.simplevisit;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MyClassVisit extends ClassVisitor {

    public MyClassVisit() {
        super(Opcodes.ASM5);
    }

    /**
     * Visit()方法参数说明：
     *
     * version：类文件的版本号，表示类文件的JDK版本。例如，JDK1.8的版本是52（0x34），JDK 11 对应的版本为55（0x37）
     * access：类访问标志，表示类的访问权限和属性
     * name：类的内部名称，如我们这个类就是com/jack/redis/pojo/MyClass
     * sinature：类的范型签名，如果没有范型信息，则为null
     * superName：父类的内部名。这里为java.lang.Object
     * interfaces：类实现的接口的内部名称数组
     * @param version
     * @param access
     * @param name
     * @param signature
     * @param superName
     * @param interfaces
     */
    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println("visit class:" + name);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    /**
     * visitMethod()方法参数说明：
     * access：方法访问标志
     * name：方法的名称
     * descriptor：方法的描述符，表示方法的参数类型和返回值类型
     * signature：方法的范型信息
     * exceptions：方法抛出的异常的内部名称数组
     * @param access
     * @param name
     * @param descriptor
     * @param signature
     * @param exceptions
     * @return
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        System.out.println("visit method:" + name);
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }
}
