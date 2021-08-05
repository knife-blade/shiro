package com.touchealth.platform.processengine.utils;

/**
 * 进制转换工具
 *
 * @author liufengqiang
 * @date 2020-12-03 11:08:24
 */
public class ConvertUtils {

    private static String CHARS_2 = "01";
    private static String CHARS_16 = "0123456789abcdef";
    private static String CHARS_32 = "0123456789abcdefghijklmnopqrstuv";
    private static String CHARS_62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String CHARS_72 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*-+";
    private static String CHARS_90 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()-=_+[]{}|;:~`,<.>?";

    public static void main(String[] args) {
        Long test = 1329620006031749122L;
        System.out.println("初始值：" + test);
        System.out.println("16进制：" + ConvertUtils.encode(test, 16));
        System.out.println("32进制：" + ConvertUtils.encode(test, 32));
        System.out.println("62进制：" + ConvertUtils.encode(test, 62));
        System.out.println("72进制：" + ConvertUtils.encode(test, 72));
        System.out.println("90进制：" + ConvertUtils.encode(test, 90));
    }

    /**
     * Long转指定进制字符串
     *
     * @return
     */
    public static String encode(Long source, int scale) {
        String chars = getScale(scale);
        StringBuilder sb = new StringBuilder();
        long result;

        while (source > scale - 1) {
            result = source % scale;
            sb.append(chars.charAt((int) result));
            source = source / scale;
        }

        sb.append(chars.charAt(source.intValue()));
        return sb.reverse().toString();
    }

    public static String encode62(Long source) {
        return encode(source, 62);
    }

    public static long decode(String source, int scale) {
        String chars = getScale(scale);
        long num = 0;
        int index;
        for (int i = 0; i < source.length(); i++) {
            // 查找字符的索引位置
            index = chars.indexOf(source.charAt(i));
            // 索引位置代表字符的数值
            num += (long) (index * (Math.pow(scale, source.length() - i - 1)));
        }
        return num;
    }

    public static long decode62(String source) {
        return decode(source, 62);
    }

    private static String getScale(int scale) {
        switch (scale) {
            case 2:
                return CHARS_2;
            case 16:
                return CHARS_16;
            case 32:
                return CHARS_32;
            case 62:
                return CHARS_62;
            case 72:
                return CHARS_72;
            case 90:
                return CHARS_90;
            default:
                throw new RuntimeException("暂不支持该进制");
        }
    }
}
