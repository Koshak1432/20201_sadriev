import java.lang.reflect.*;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("Hell world!!");
        Class<?> c = null;
        try {
            c = java.lang.Class.forName("ClassToLoad");
        }
        catch (ClassNotFoundException e) {
            System.out.println("caught an exception!");
//            e.printStackTrace();
            return;
        }

//        System.out.println("loaded: " + c.toGenericString());    //jit (just in time compilation)
        Object o = c.getDeclaredConstructor().newInstance();
        System.out.println(o.toString());

        for (Method method : c.getDeclaredMethods()) {
            System.out.println(method);

            if (method.getName().equals("methodToInvoke")) {
                method.invoke(o);
            }
        }

//        ArrayList code;
//        ExecutionContext ctx;
//        while (true) {
//            char c = ...;
//
//            ICommand cmd = (ICommand) Factory.createCmdByChar(c); //
//            cmd.execute(ctx);
//        }
    }
}

//сделать кэш комманд, чтобы каждый раз не дёргать
//мапу комманд
//class Factory {
//    Object createCmdByChar(char c) {
//        Class c = Class.forName(" " + c);
//        return c.getDeclaredConstructor().newInstance();
//    }
//}

//есть интерфейс комманда, там метод execute(), без каких-то ифов в главном цикле интерпретатора, он должен
// передавать символы
//аккуратно попилить прогу на классы, не смешивать всё со всем