package main.java.utils.pow;

/**
 * pow的运算
 */
public class PowDemo {
    /**
     * 幂运算
     * @param x 底数
     * @param n 幂
     * @return
     * 注意下标溢出的风险
     */
    public static long pwd(int x,int n){
        long res = 1;
        for(;n > 0 ; n /= 2){
            if (n % 2 == 1){
                res *= x;
            }
            x *= x;
        }
        return res;
    }
    /**
     * 幂运算,取模
     * @param x 底数
     * @param n 幂
     * @return
     */
    public static long pwd(int x,int n,int mod){
        long res = 1;
        for(;n > 0 ; n /= 2){
            if (n % 2 == 1){
                res =res * x % mod;
            }
            x = x * x % mod;
        }
        return res;
    }
}
