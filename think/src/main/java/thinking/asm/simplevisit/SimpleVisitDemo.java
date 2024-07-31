package thinking.asm.simplevisit;

import jdk.internal.org.objectweb.asm.ClassReader;

import java.nio.file.Files;
import java.nio.file.Paths;

public class SimpleVisitDemo {
    public static void main(String[] args) throws Exception{
        //从文件系统中加载字节码
        byte[] bytes = Files.readAllBytes(Paths.get("/Users/mengsong/IdeaProjects/thinking/think/target/classes/thinking/asm/simplevisit/MyClass.class"));
        //创建ClassReader实例
        ClassReader classReader=new ClassReader(bytes);
        MyClassVisit myClassVisit = new MyClassVisit();
        classReader.accept(myClassVisit, 0);
    }
}
