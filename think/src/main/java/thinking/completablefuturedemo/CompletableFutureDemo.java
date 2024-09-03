package thinking.completablefuturedemo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * CompletableFuture的使用样例
 */
public class CompletableFutureDemo {
    static ThreadPoolExecutor executor = new ThreadPoolExecutor(2,2,10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(5),
            new ThreadPoolExecutor.DiscardPolicy());
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) throws Exception{
        test();
        executor.shutdown();
    }
    public static void test() throws Exception{
        CompletableFuture<Void> orderFuture = CompletableFuture.supplyAsync(() -> UserVo.verifyUser("user123"), executor)
                .thenCombineAsync(CompletableFuture.supplyAsync(() -> UserVo.checkInventory("item456"), executor), (userVerified, inventoryChecked) -> {
                    if (userVerified && inventoryChecked) {
                        return UserVo.processPayment("user123","item456");
                    } else {
                        throw new RuntimeException("User verification or inventory check failed");
                    }
                }, executor)
                .thenApplyAsync(paymentProcessed -> UserVo.generateOrder("user123", "item456"), executor)
                .thenAcceptAsync(order -> System.out.println("Order completed: " + order), executor)
                .exceptionally(ex -> {
                    System.err.println("Order processing failed: " + ex.getMessage());
                    return null;
                });
        orderFuture.join(); // 等待所有操作完成
    }
    public static void testTimeOut() throws Exception{
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "任务执行结束";
        });
        //Java 9引入了orTimeout和completeOnTimeout方法：
        /*future.orTimeout(1, TimeUnit.SECONDS)
                .exceptionally(ex -> "Timeout occurred: " + ex.getMessage())
                .thenAccept(System.out::println);*/
    }
    /**
     * 异步执行和计算结果
     * 1.runAsync：执行不返回结果的异步任务。
     * 2.supplyAsync：执行并返回结果的异步任务。
     * 3.thenApply：当CompletableFuture完成时，对其结果进行处理，并返回一个新的CompletableFuture。
     * 4.thenAccept：当CompletableFuture完成时，消费其结果，但不返回新的CompletableFuture。
     * 5.thenRun：当CompletableFuture完成时，运行一个任务，不关心其结果。
     * 6.thenCombine：当两个CompletableFuture都完成时，对其结果进行处理，并返回一个新的CompletableFuture。
     * 7.thenCompose：用于将一个CompletableFuture的结果作为另一个CompletableFuture的输入，类似于flatMap：
     * 8.allOf：等待所有提供的CompletableFuture都完成。
     * 9.anyOf：只要任意一个CompletableFuture完成即可。
     * 10.handle方法用于处理正常结果和异常情况。
     * 11.exceptionally方法仅处理异常情况.
     */
    public static void testExecuteAndCalculate() throws Exception{
        //1.runAsync
        CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> System.out.println("runAsync"),executor);
        System.out.println(runAsync.get());//null
        //2.supplyAsync
        CompletableFuture<Integer> supplyAsync = CompletableFuture.supplyAsync(() -> 1 + 2);
        System.out.println(supplyAsync.get());
        //3.thenApply
        CompletableFuture<Integer> thenApply = supplyAsync.thenApply(a -> a + 3);
        System.out.println(thenApply.get());
        //4.thenAccept
        supplyAsync.thenAccept(System.out::println);
        //5.thenRun
        supplyAsync.thenRun(()-> System.out.println("new Runnable"));
        //6.thenCombine
        CompletableFuture<Integer> thenCombine1 = CompletableFuture.supplyAsync(() -> 1 + 2);
        CompletableFuture<Integer> thenCombine2 = CompletableFuture.supplyAsync(() -> 3 + 2);
        CompletableFuture<Integer> thenCombine = thenCombine1.thenCombine(thenCombine2, (a, b) -> a - b);
        System.out.println(thenCombine.get());
        //7.thenCompose
        CompletableFuture<Integer> thenCompose = supplyAsync.thenCompose(a -> CompletableFuture.supplyAsync(() -> a + 3));
        System.out.println(thenCompose.get());
    }
    public static void testHandle() throws Exception{
        CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> {
            if (Math.random() < 0.5) {
                throw new RuntimeException("error");
            }
            return "运行结束";
        }, executor);
        supplyAsync.handle((a, b) -> {
                    if (b != null) {
                        return "发生异常";
                    }
                    return a;
                }
        ).thenAccept(System.out::println);
        supplyAsync.exceptionally(Throwable::getMessage).thenAccept(System.out::println);
    }
    public static void testAllOf() throws Exception{
        CompletableFuture<Void> demo1 = CompletableFuture.runAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },executor);
        CompletableFuture<String> demo2 = CompletableFuture.supplyAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "执行结束";
        },executor);
        System.out.println(sdf.format(new Date()));
        CompletableFuture<Void> future = CompletableFuture.allOf(demo1, demo2).thenRun(() -> {
            System.out.println(sdf.format(new Date()));
            System.out.println("执行结束");
        });
    }
    public static void testAnyOf() throws Exception{
        CompletableFuture<Void> demo1 = CompletableFuture.runAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        },executor);
        CompletableFuture<String> demo2 = CompletableFuture.supplyAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "执行结束";
        },executor);
        System.out.println(sdf.format(new Date()));
        CompletableFuture<Void> future = CompletableFuture.anyOf(demo1, demo2).thenRun(() -> {
            System.out.println(sdf.format(new Date()));
            System.out.println("执行结束");
        });
    }
    /**
     * complete()方法在任务完成时设置结果，如果任务已经完成，则无效
     * @throws Exception
     */
    public static void testCreate() throws Exception{
        //1.静态工厂
        CompletableFuture<String> demo1 = CompletableFuture.supplyAsync(() -> "Hello");
        demo1.complete("World");//无效
        System.out.println(demo1.get());
        //2.new
        CompletableFuture<String> demo2 = new CompletableFuture<>();
        demo2.complete("Hello");
        demo2.complete("World");//无效
        System.out.println(demo2.get());
        //3.异常
        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Something went wrong"));
        future.get();
    }
}
class UserVo{
    public static boolean verifyUser(String userId) {
        // 模拟用户验证
        System.out.println("Verifying user: " + userId);
        return true;
    }
    public static boolean checkInventory(String itemId) {
        // 模拟库存检查
        System.out.println("Checking inventory for item: " + itemId);
        return true;
    }
    public static boolean processPayment(String userId, String itemId) {
        // 模拟支付处理
        System.out.println("Processing payment for user: " + userId + " and item: " + itemId);
        return true;
    }
    public static String generateOrder(String userId, String itemId) {
        // 模拟订单生成
        System.out.println("Generating order for user: " + userId + " and item: " + itemId);
        return "Order123";
    }
}
