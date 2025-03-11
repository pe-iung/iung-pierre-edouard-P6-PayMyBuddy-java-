package com.P6.P6.utils;

import java.util.regex.Pattern;

public class EmailValidation {

    /**
     * use a regex to determine if an email is valid
     * will be used at signup time
     * @param emailAddress, regexPattern
     * @return boolean
     */
    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }
}
