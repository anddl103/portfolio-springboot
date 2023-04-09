package com.hybe.larva.util;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class LocaleUtil {

    private static final List<String> supportedLanguages = Arrays.asList(
            Locale.ENGLISH.getLanguage(), // default
            Locale.KOREAN.getLanguage(),
            Locale.JAPANESE.getLanguage()
    );

    public static String getSupportedLanguage(Locale locale) {
        String reqLang = locale.getLanguage();
        return getSupportedLanguage(reqLang);
    }

    public static String getSupportedLanguage(String reqLang) {
        if (supportedLanguages.contains(reqLang)) {
            return reqLang;
        } else {
            return supportedLanguages.get(0);
        }
    }

}
