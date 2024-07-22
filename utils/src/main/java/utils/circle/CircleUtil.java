package main.java.utils.circle;

/**
 * 圆的工具类
 */
public class CircleUtil {
    // 判断两个圆是否相交
    public static boolean areCirclesIntersecting(double center1X, double center1Y, double radius1,
                                                 double center2X, double center2Y, double radius2) {
        double distanceSquared = Math.pow(center1X - center2X, 2) + Math.pow(center1Y - center2Y, 2);
        double sumOfRadiiSquared = Math.pow(radius1 + radius2, 2);
        return distanceSquared <= sumOfRadiiSquared;
    }
    // 判断圆是否包含点
    public static boolean containsPoint(double circleX, double circleY, double radius, double pointX, double pointY) {
        double distanceSquared = Math.pow(circleX - pointX, 2) + Math.pow(circleY - pointY, 2);
        double radiusSquared = Math.pow(radius, 2);
        return distanceSquared <= radiusSquared;
    }
}
