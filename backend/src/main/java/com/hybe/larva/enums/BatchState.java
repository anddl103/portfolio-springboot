package com.hybe.larva.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BatchState {

    INVALID(-1, "비정상"),
    ON(0, "ON"),
    WORKING(1, "작업중"),
    OFF(2, "OFF")
    ;

    private final int code;
    private final String description;
}
