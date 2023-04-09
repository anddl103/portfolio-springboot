package com.hybe.larva.exception;

public class DeletedResourceException extends RuntimeException {

    public DeletedResourceException(String message) {
        super(message);
    }
}
