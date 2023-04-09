package com.hybe.larva.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ContentsType {
    STILL("STILL", "Still type"),
    LIVE("LIVE", "Live type"),
    VIDEO("VIDEO", "Video type");

    private final String code;
    private final String description;
}
