package com.hybe.larva.exception;

public class DownloadLimitException extends RuntimeException {

    public DownloadLimitException(String message) {
        super(message);
    }
}
