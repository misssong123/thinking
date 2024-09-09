package thinking.threadpooldemo.monitor;
import thinking.threadpooldemo.monitor.customer.CustomCaller;
import thinking.threadpooldemo.monitor.execute.ThreadExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MonitorDemo {
    static Random random = new Random(37);
    static String name = "detailCardThreadPool";
    public static void main(String[] args) {
        ThreadExecutor.registerExecutor(getCustomThreadConfig());
        while (true){
            int num = random.nextInt(100)+1;
            List<CustomCaller<String> > taskList = new ArrayList<>();
            for(int i = 0 ; i < num ; i++){
                int finalI = i;
                taskList.add(new CustomCaller<String>("任务"+ finalI) {
                    @Override
                    public String process() throws Exception {
                        TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
                        return "任务"+ finalI +"处理完成";
                    }
                });
            }
            ThreadExecutor.batchSubmitTask(name,taskList);
            try{
                TimeUnit.SECONDS.sleep(2);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    public static String getCustomThreadConfig(){
        String jsonStr = "{\n" +
                "\t\"avgActiveCountId\": 105582,\n" +
                "\t\"avgLargestPoolSizeId\": 105585,\n" +
                "\t\"avgPoolSizeId\": 105588,\n" +
                "\t\"avgQueueSizeId\": 105579,\n" +
                "\t\"avgTaskUseTimeId\": 105591,\n" +
                "\t\"coreSize\": 10,\n" +
                "\t\"description\": \"卡片线程池\",\n" +
                "\t\"initialQueueSize\": 150,\n" +
                "\t\"keepAliveTime\": 100,\n" +
                "\t\"maxActiveCountId\": 105581,\n" +
                "\t\"maxLargestPoolSizeId\": 105584,\n" +
                "\t\"maxPoolSizeId\": 105587,\n" +
                "\t\"maxQueueSizeId\": 105578,\n" +
                "\t\"maxSize\": 10,\n" +
                "\t\"maxTaskUseTimeId\": 105590,\n" +
                "\t\"minActiveCountId\": 105583,\n" +
                "\t\"minLargestPoolSizeId\": 105586,\n" +
                "\t\"minPoolSizeId\": 105589,\n" +
                "\t\"minQueueSizeId\": 105580,\n" +
                "\t\"minTaskUseTimeId\": 105592,\n" +
                "\t\"monitor\": true,\n" +
                "\t\"needQueueAlarm\": false,\n" +
                "\t\"printLog\": false,\n" +
                "\t\"rejectedExecutionHandlerName\": \"CallerRunsPolicy\",\n" +
                "\t\"threadPoolName\": \"detailCardThreadPool\",\n" +
                "\t\"timeUnit\": \"SECONDS\"\n" +
                "}";
        return jsonStr;
    }
}


