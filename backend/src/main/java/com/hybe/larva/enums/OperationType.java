package com.hybe.larva.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OperationType {
    INSERT("INSERT", "Insert type"),
    UPDATE("UPDATE", "Update Type"),
    DELETE("DELETE", "Delete type"),
    NONE("NONE", "None type");

    private final String code;
    private final String description;
}
