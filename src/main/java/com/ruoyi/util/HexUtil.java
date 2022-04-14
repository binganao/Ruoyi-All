package com.ruoyi.util;

public class HexUtil {
    public static String bytes2HexString(byte[] b) {

        StringBuffer result = new StringBuffer();

        for (int i = 0; i < b.length; i++) {

            result.append(String.format("%02X", b[i]));

        }

        return result.toString();

    }

    /**
     * @param src 16进制字符串
     * @return 字节数组
     * @Title:hexString2Bytes
     * @Description:16进制字符串转字节数组
     */

    public static byte[] hexString2Bytes(String src) {

        int l = src.length() / 2;

        byte[] ret = new byte[l];

        for (int i = 0; i < l; i++) {

            ret[i] = Integer.valueOf(src.substring(i * 2, i * 2 + 2), 16).byteValue();

        }

        return ret;

    }

    /**
     * @param strPart    字符串
     * @return 16进制字符串
     * @Title:string2HexString
     * @Description:字符串转16进制字符串
     */

    public static String string2HexString(String strPart) {

        try {

            return bytes2HexString(strPart.getBytes());

        } catch (Exception e) {

            return "";

        }

    }

    /**
     * @param src 16进制字符串
     * @return 字节数组
     * @Title:hexString2String
     * @Description:16进制字符串转字符串
     */

    public static String hexString2String(String src) {

        try {

            byte[] bts = hexString2Bytes(src);
            return new String(bts);

        } catch (Exception e) {

            return src;

        }

    }

}
