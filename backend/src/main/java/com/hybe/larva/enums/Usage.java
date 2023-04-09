package com.hybe.larva.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hybe.larva.entity.user_info.AccountAgree;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Usage {

    TERMS_OF_SERVICE("TERMS_OF_SERVICE", "이용 약관"),
    SERVICE_OPERATION_POLICY("SERVICE_OPERATION_POLICY", "서비스 운영 정책"),
    PRIVACY_POLICY("PRIVACY_POLICY", "개인 정보 처리"),
    PUSH("PUSH", "푸시"),
    OPEN_SOURCE_LICENSE("OPEN_SOURCE_LICENSE", "오픈소스 라이센스");

    private final String code;
    private final String description;
}
