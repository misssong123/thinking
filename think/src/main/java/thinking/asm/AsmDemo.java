package thinking.asm;

import thinking.asm.generator.HelloWorldGenerator;

public class AsmDemo {
    public static void main(String[] args) {
        try {
            byte[] bytes = HelloWorldGenerator.generateHelloWorldClass("HelloWorld");
            CustomerClassLoader loader = new CustomerClassLoader();
            Class<?> helloWorldClass = loader.defineClass("HelloWorld", bytes);
            helloWorldClass.getMethod("main",String[].class).invoke(null,(Object)args);
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public static class CustomerClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return super.defineClass(name, b, 0, b.length);
        }
    }
}
