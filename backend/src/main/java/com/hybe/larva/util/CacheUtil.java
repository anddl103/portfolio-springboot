package com.hybe.larva.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Configuration
@AllArgsConstructor
public class CacheUtil {

    private final CacheManager cacheManager;
    private static String name = "product";
    public static final String IS_JOB_ENABLED = "isJobEnabled";
    public static final String DEFAULT_COUNTRY_CODE = "en";

    private String getCache(String countryCode, String key) {

        if (cacheManager.getCache(name).get(countryCode) == null) {

            countryCode = DEFAULT_COUNTRY_CODE;
        }

        Map<String, String> map = (Map<String, String>) cacheManager.getCache(name).get(countryCode).get();

        if (!map.containsKey(key)) {

            if (countryCode.equals(DEFAULT_COUNTRY_CODE)) {
                return "";
            }
            map = (Map<String, String>) cacheManager.getCache(name).get(DEFAULT_COUNTRY_CODE).get();
            if (!map.containsKey(key)) {
                return "";
            }
        }

        return map.get(key);
    }

    private String getCacheCountryCode(String countryCode, String key) {
        Map<String, String> map = (Map<String, String>) cacheManager.getCache(name).get(countryCode).get();
        if (!map.containsKey(key)) {
            return "";
        }
        return map.get(key);
    }

    public String getLanguageResponse(String key) {
        return getCache(getLanguage(), key);
    }

    public String getLanguageResponse(String countryCode, String key) {
        return getCache(countryCode, key);
    }

    private Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

    public String getLanguage() {
        String countryCode = getLocale().getLanguage();
        if (cacheManager.getCache(name).get(countryCode) == null) {
            countryCode = DEFAULT_COUNTRY_CODE;
        }
        return countryCode;
    }

    public String getLanguage(String lang) {

        String countryCode = lang;

        if (lang == null || lang.equals("") || lang.trim().equals("")) {
            countryCode = getLocale().getLanguage();
        }

        if (cacheManager.getCache(name).get(countryCode) == null) {
            countryCode = DEFAULT_COUNTRY_CODE;
        }

        return countryCode;
    }

    public Map<String, Object> getAllLanguageResponse(String key) {

        Map<String, String> values = new HashMap();

        Object obj = cacheManager.getCache(name).getNativeCache();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> data = objectMapper.convertValue(obj, Map.class);

        data.forEach((k, v) -> {
            if (!k.equals(IS_JOB_ENABLED)) {
                String value = getCacheCountryCode(k, key);
                
                if (value != null) {
                    values.put(k, value);
                }
            }
        });

        Map<String, Object> map = new HashMap();
        map.put("id", key);
        map.put("values", values);

        return map;
    }

    public boolean isJobEnabled() {

        if (cacheManager.getCache(name).get(IS_JOB_ENABLED) == null) {
            return true;
        }

        boolean isJobEnabled = (boolean) cacheManager.getCache(name).get(IS_JOB_ENABLED).get();

        return isJobEnabled;
    }


}
