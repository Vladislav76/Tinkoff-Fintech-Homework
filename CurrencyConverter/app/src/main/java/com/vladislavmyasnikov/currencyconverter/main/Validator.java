package com.vladislavmyasnikov.currencyconverter.main;

public class Validator {

    /**
     * Validates an input string.
     * The string can contain only numbers and the point symbol.
     * The following should be done:
     * 1) the string cannot be empty;
     * 2) the point symbol must not be the first or last character in the string.
     *
     * @param s an input string
     * @return boolean of the correctness of a given string
     */
    public static boolean isValidInputValue(String s) {
        if (s.equals("")) {
            return false;
        }

        char[] chars = s.toCharArray();
        boolean wasDot = false;

        for (int i = 0; i < s.length(); i++) {
            if (chars[i] == '.') {
                if (wasDot || i == 0 || i == s.length() - 1) {
                    return false;
                } else {
                    wasDot = true;
                }
            } else if (chars[i] < '0' || chars[i] > '9') {
                return false;
            }
        }
        return true;
    }
}
