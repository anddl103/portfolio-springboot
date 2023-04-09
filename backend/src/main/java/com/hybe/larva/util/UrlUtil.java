package com.hybe.larva.util;

import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Slf4j
public class UrlUtil {

    public static String buildUrl(String base, Map<String, Object> params) {
        return params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + UrlUtil.encodeValue(String.valueOf(entry.getValue())))
                .collect(joining("&", base + "?", ""));
    }

    public static String encodeValue(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.error("Failed to url encode: value={}", value, e);
            return "";
        }
    }
}
