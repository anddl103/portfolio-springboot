package com.hybe.larva.dto.common;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ToString
@Getter
public class UpdateTokenResponse {

    @NotBlank
    private final String accessToken;

    @NotBlank
    private final String refreshToken;

    public UpdateTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
