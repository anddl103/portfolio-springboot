package com.hybe.larva.exception;

public class ResourceAlreadyInUseException extends RuntimeException {

    public ResourceAlreadyInUseException(String from, String target) {
        super(String.format("이미 %s 에서 사용중인 %s 입니다.", from, target));
    }

    public ResourceAlreadyInUseException(String str) {
        super(str);
    }
}
