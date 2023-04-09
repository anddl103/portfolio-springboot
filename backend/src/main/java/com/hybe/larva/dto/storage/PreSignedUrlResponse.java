package com.hybe.larva.dto.storage;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@ToString
@Getter
public class PreSignedUrlResponse {

    // s3 object key
    @NotBlank
    private final String key;

    // object url
    @NotBlank
    private final String url;

    // expiration date
    @NotNull
    private final LocalDateTime expAt;

    public PreSignedUrlResponse(String key, String url, LocalDateTime expAt) {
        this.key = key;
        this.url = url;
        this.expAt = expAt;
    }
}
