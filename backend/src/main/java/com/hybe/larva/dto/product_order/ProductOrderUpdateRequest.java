package com.hybe.larva.dto.product_order;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@Builder
public class ProductOrderUpdateRequest {

    @Min(1)
    @NotNull
    private final Long quantity;

    @NotBlank
    private final String comment;

}
