package com.hybe.larva.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AlbumState {

    INVALID(-1, "비정상"),
    WORKING(1, "작업중"),
    SUBMITTED(2, "제출됨"),
    REJECTED(3, "반려됨"),
    REVIEWING(4, "리뷰중"),
    CONFIRMED(5, "승인됨"),
    DEPLOYING(6, "배포중"),
    DEPLOYED(7, "배포됨")
    ;

    private final int code;
    private final String description;
}
