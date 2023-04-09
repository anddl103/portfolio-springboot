package com.hybe.larva.util;

import org.springframework.web.multipart.MultipartFile;

public class CsvHelper {

    public static boolean isCsvType(MultipartFile file) {
        return "text/csv".equals(file.getContentType())
                || "application/vnd.ms-excel".equals(file.getContentType());
    }
}
