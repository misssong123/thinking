package thinking.asm.modifyvisit;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ModifyClassDemo {
    public static void main(String[] args) throws Exception{
        //从文件系统中加载字节码
        byte[] originBytes = Files.readAllBytes(Paths.get("/Users/mengsong/IdeaProjects/thinking/think/target/classes/thinking/asm/modifyvisit/MyClass.class"));
        //ClassReader
        ClassReader classReader = new ClassReader(originBytes);
        //ClassWriter
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        MyClassVisit myClassVisit = new MyClassVisit(classWriter);
        classReader.accept(myClassVisit,ClassReader.SKIP_CODE);
        byte[] modifyBytes = classWriter.toByteArray();
        //保存修改文件
        FileOutputStream fos = new FileOutputStream("ModifyClass.class");
        fos.write(modifyBytes);
        fos.close();
        //运行修改后的类
        MyClass modifiedClass = new MyClass();
        System.out.println(modifiedClass);
    }
}
