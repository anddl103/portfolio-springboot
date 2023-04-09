package com.hybe.larva.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ProductOrderStatus {
    CREATED("CREATED", "생산 가능"),
    COMPLETE("COMPLETE", "생산 완료"),
    CANCELLED("CANCELLED", "주문 취소");

    private final String code;
    private final String description;
}
