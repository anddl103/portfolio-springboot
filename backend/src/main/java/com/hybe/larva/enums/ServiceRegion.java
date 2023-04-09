package com.hybe.larva.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ServiceRegion {
    GLOBAL("GLOBAL", "Global service");
//    CHINA("China service");

    private final String code;
    private final String description;

//    public static boolean isChina(ServiceRegion region) {
//        return CHINA == region;
//    }
}
