package com.hybe.larva.dto.admin;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ToString
@Data
@Builder
public class AdminUpdateRequest {

    @NotBlank
    private String uid;


    private String password;

    @NotBlank
    private String role;
}
