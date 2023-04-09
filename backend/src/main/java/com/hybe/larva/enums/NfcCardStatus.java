package com.hybe.larva.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NfcCardStatus {

    BEFORE_ORDER("BEFORE_ORDER", "before Order."),
    AFTER_ORDER("AFTER_ORDER", "after Order.");

    private final String code;
    private final String description;
}
