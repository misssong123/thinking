package netty.wconfig.client.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;


public class EncryptUtil {
    private static final Logger log = LoggerFactory.getLogger(EncryptUtil.class);

    private static MessageDigest md5;

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            log.error("init md5-instance error.", e);
        }
    }

    public static String encrypt(String original) {
        byte[] md5Bytes = md5.digest(original.getBytes());
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
