package com.hybe.larva.exception;

public class InvalidProductOrderException extends RuntimeException {

    public InvalidProductOrderException(String message) {
        super(message);
    }
}
