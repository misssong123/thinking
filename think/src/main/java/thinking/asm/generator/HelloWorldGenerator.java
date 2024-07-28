package thinking.asm.generator;

import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

public class HelloWorldGenerator {
    public static byte[] generateHelloWorldClass(String className){
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        //定义
        cw.visit(Opcodes.V1_8,Opcodes.ACC_PUBLIC,className,null,"java/lang/Object",null);
        //init方法
        MethodVisitor initMethod = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        initMethod.visitCode();
        initMethod.visitVarInsn(Opcodes.ALOAD,0);
        initMethod.visitMethodInsn(Opcodes.INVOKESPECIAL,"java/lang/Object","<init>","()V",false);
        initMethod.visitInsn(Opcodes.RETURN);
        initMethod.visitMaxs(1,1);
        initMethod.visitEnd();
        //main方法
        MethodVisitor mainMethod = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        mainMethod.visitCode();
        mainMethod.visitFieldInsn(Opcodes.GETSTATIC,"java/lang/System","out","Ljava/io/PrintStream;");
        mainMethod.visitLdcInsn("Hello, world!");
        mainMethod.visitMethodInsn(Opcodes.INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V",false);
        mainMethod.visitInsn(Opcodes.RETURN);
        mainMethod.visitMaxs(2,2);
        mainMethod.visitEnd();
        cw.visitEnd();
        return cw.toByteArray();
    }
}
