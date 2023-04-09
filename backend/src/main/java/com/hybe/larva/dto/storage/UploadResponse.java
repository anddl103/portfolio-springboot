package com.hybe.larva.dto.storage;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@Builder
public class UploadResponse {

    // s3 object key
    @NotBlank
    private final String key;

    // object url
    @NotBlank
    private final String url;

    // public read permission flag
    @NotNull
    private final Boolean publicRead;


    public UploadResponse(String key, String url, Boolean publicRead) {
        this.key = key;
        this.url = url;
        this.publicRead = publicRead;
    }
}
