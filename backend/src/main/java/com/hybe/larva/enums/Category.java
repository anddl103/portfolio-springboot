package com.hybe.larva.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Category {

    ACCOUNT("ACCOUNT", "계정 관련"), //Account related
    PHOTO_CARD("PHOTO_CARD", "포토카드 관련"), //Photo card
    APPLY_CONTENT("APPLY_CONTENT", "컨텐츠 적용"), //Apply content
    APP("APP", "앱 사용 방법"); // App

    private final String code;
    private final String description;
}
