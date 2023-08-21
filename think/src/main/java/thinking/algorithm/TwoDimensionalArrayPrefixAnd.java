package thinking.algorithm;

import com.alibaba.fastjson.JSONObject;

/**
 * 二维数组前缀和
 */
public class TwoDimensionalArrayPrefixAnd {
    private int[][] sum;

    /**
     * 二维数组前缀和
     * @param matrix
     * @return
     */
    public int[][] preAddSum(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        sum = new int[m + 1][n + 1]; // 注意：如果 matrix[i][j] 范围很大，需要使用 long
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                sum[i + 1][j + 1] = sum[i + 1][j] + sum[i][j + 1] - sum[i][j] + matrix[i][j];
            }
        }
        return sum;
    }

    /**
     * 二维数组后缀和
     * @param matrix
     * @return
     */
    public int[][] sufAddSum(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[][] res = new int[m + 1][n + 1]; // 注意：如果 matrix[i][j] 范围很大，需要使用 long
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                res[i][j] = res[i + 1][j] + res[i][j + 1] - res[i + 1][j + 1] + matrix[i][j];
            }
        }
        return res;
    }
    /**
     * 返回左上角在 (r1,c1) 右下角在 (r2-1,c2-1) 的子矩阵元素和（类似前缀和的左闭右开）
     * @param r1
     * @param c1
     * @param r2
     * @param c2
     * @return
     */
    public int query(int r1, int c1, int r2, int c2) {
        return sum[r2][c2] - sum[r2][c1] - sum[r1][c2] + sum[r1][c1];
    }

    /**
     * 返回左上角在 (r1,c1) 右下角在 (r2,c2) 的子矩阵元素和（类似前缀和的左闭右闭）
     * @param r1
     * @param c1
     * @param r2
     * @param c2
     * @return
     */
    public int query2(int r1, int c1, int r2, int c2) {
        return sum[r2 + 1][c2 + 1] - sum[r2 + 1][c1] - sum[r1][c2 + 1] + sum[r1][c1];
    }

    public static void main(String[] args) {
        TwoDimensionalArrayPrefixAnd demo = new TwoDimensionalArrayPrefixAnd();
        int[][] matrix = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
        System.out.println(JSONObject.toJSONString(demo.preAddSum(matrix)));
        System.out.println(JSONObject.toJSONString(demo.sufAddSum(matrix)));
    }
}
