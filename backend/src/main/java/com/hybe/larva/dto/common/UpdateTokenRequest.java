package com.hybe.larva.dto.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ToString
@Getter
@NoArgsConstructor
public class UpdateTokenRequest {

    @NotBlank
    private String refreshToken;

}
