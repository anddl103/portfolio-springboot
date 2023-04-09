package com.hybe.larva.exception;

public class S3FileUploadException extends RuntimeException {

    public S3FileUploadException(String message) {
        super(message);
    }
}
