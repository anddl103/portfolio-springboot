package com.hybe.larva.dto.product_key;

import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@ToString
@Getter
public class ProductKeyRegisterRequest {

    @NotBlank
    private String code;
}
