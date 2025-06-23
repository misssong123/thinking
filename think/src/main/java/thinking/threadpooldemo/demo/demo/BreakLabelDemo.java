package thinking.threadpooldemo.demo.demo;

/**
 * 标签demo
 */
public class BreakLabelDemo {
    public static void main(String[] args) {
        retry:
        for(int i = 0 ; i < 10 ; i++){
            for(int j = 0 ; j < 1 ; j++){
                if (i > 8){
                    break retry;
                }
                if (i % 2 == 0){
                    continue retry;
                }
                System.out.println("i:"+i+";j:"+j);
            }
        }
        System.out.println("end");
    }
}
