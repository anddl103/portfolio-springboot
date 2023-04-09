package com.hybe.larva.dto.product_order;

import com.hybe.larva.enums.ServiceRegion;
import com.hybe.larva.entity.product_order.ProductOrder;
import com.hybe.larva.enums.ProductOrderStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@ToString
@Getter
@Builder
public class ProductOrderAddRequest {


    // Nfc_Card.id
    @NotBlank
    private String nfcCardId;

    @Min(1)
    @NotNull
    private Long quantity;

    @NotBlank
    private String comment;

    @ApiModelProperty(dataType = "java.lang.String")
    private ServiceRegion region;


    public ProductOrder toEntity() {
        return ProductOrder.builder()
                .nfcCardId(nfcCardId)
                .quantity(quantity)
                .comment(comment)
                .region(region)
                .status(ProductOrderStatus.CREATED)
                .build();
    }
}
