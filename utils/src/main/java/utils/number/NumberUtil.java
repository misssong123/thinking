package main.java.utils.number;

import org.apache.commons.lang3.StringUtils;

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

}
