package main.java.utils.number;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.regex.Pattern;

public class NumberUtil {
    public static final Pattern doublePattern = Pattern.compile("^[-\\+]?[.\\d]*$");

    /**
     * 判断字符串是否为double
     * @param str 字符串
     * @return
     */
    public static boolean isDouble(String str) {
        if (StringUtils.isBlank(str)) {
            return false;
        }
        return doublePattern.matcher(str).matches();
    }
    /**
     * 是否为质数
     */
    public static boolean isPrime(int n) {
        if (n <= 1) {
            return false;
        }
        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     *  生成一个指定长度的质数数组
     * @param n
     * @return
     */
    public static boolean[] generatePrimeArray(int n) {
        boolean[] isPrime = new boolean[n+1];
        Arrays.fill(isPrime, true); // 初始化全部数为质数
        isPrime[0] = false; // 0不是质数
        isPrime[1] = false; // 1不是质数

        for (int i = 2; i <= Math.sqrt(n); i++) {
            if (isPrime[i]) { // i是质数，则将i的倍数标记为非质数
                for (int j = i * i; j <= n; j += i) {
                    isPrime[j] = false;
                }
            }
        }

        return isPrime;
    }
    /**
     * 最大公约数
     * @param a
     * @param b
     * @return
     */
    public static int gcd(int a, int b) {
        while (b != 0) {
            int temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }
}
