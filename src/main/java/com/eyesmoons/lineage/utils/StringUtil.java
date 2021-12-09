package com.eyesmoons.lineage.utils;

public class StringUtil {

    /**
     * 判断是否为数字
     * @param str 要验证的字符
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        return str.chars().allMatch(Character::isDigit);
    }
}
