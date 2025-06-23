package main.java.utils.palindrome;

/**
 * 回文数样例
 */
public class Palindrome {
    /*
    * 判断一个数是否是k进制回文数
     */
    public static boolean isKPalindrome(long x, int k) {
        if (x % k == 0) {
            return false;
        }
        //右侧一半
        int rev = 0;
        while (rev < x / k) {
            rev = rev * k + (int) (x % k);
            x /= k;
        }
        return rev == x || rev == x / k;
    }
    //计算十进制的回文数
    public static void palindrome() {
        for (int base = 1; base < 100; base *= 10) {
            // 生成奇数长度回文数，例如 base = 10，生成的范围是 101 ~ 999
            for (int i = base; i < base * 10; i++) {
                long x = i;
                for (int t = i / 10; t > 0; t /= 10) {
                    x = x * 10 + t % 10;
                }
                System.out.println(x);
            }
            // 生成偶数长度回文数，例如 base = 10，生成的范围是 1001 ~ 9999
            for (int i = base; i < base * 10; i++) {
                long x = i;
                for (int t = i; t > 0; t /= 10) {
                    x = x * 10 + t % 10;
                }
                System.out.println(x);
            }
        }
    }

    public static void main(String[] args) {
        palindrome();
    }

}
