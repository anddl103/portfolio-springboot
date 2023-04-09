package com.hybe.larva.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {

    NONE(0, "no error"),
    ACCESS_DENIED(-1, "Access denied"),
    INVALID_REQUEST(-2, "http method not supported or invalid argument"),
    DUPLICATED_KEY(-3, "duplicated document id"),
    NO_RESOURCE(-4, "no resource found"),
    INVALID_CONTENT_TYPE(-5, "invalid content type"),
    INSUFFICIENT_PRODUCT_KEY(-6, "insufficient product key, please contact product key or production manager"),
    UNAUTHORIZED(-7, "unauthorized access"),
    DOWNLOAD_LIMIT(-8, "The content download limit has been reached."),
    ILLEGAL_STATE(-9, "illegal state"),
    INVALID_SERIAL(-10, "invalid serial which means unassigned, not confirmed or deleted"),
    UPLOAD_SIZE_LIMIT(-11, "the maximum upload size has been exceeded."),
    ACCESS_TOKEN_EXPIRED(-12, "access token is expired"),
    REFRESH_TOKEN_EXPIRED(-13, "refresh token is expired"),
    DELETED_RESOURCE(-14, "the resource is deleted"),
    PROTECTED_RESOURCE(-15, "this resource is protected from being deleted"),
    INTERNAL_ERROR(-16, "Internal error"),
    OPTIMISTIC_LOCKING_FAILURE(-17, "Optimistic locking failure"),
    NOT_ASSIGNED_PRODUCT_KEY(-18, "the serial is not confirmed"),
    NOT_VERIFIED_PRODUCT_KEY(-19, "the product key is not verified"),
    NOT_TAGGED_PRODUCT_KEY(-20, "the serial is not confirmed"),
    INVALID_ORDER(-21, "invalid Order, Orders that have already been completed or canceled"),

    UNKNOWN(-1000, "unhandled error");

    private final int code;
    private final String description;

}
