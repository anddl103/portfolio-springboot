package com.hybe.larva.dto.product_order;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@ToString(callSuper = true)
@Data
@Builder
public class ProductOrderCard {

    private List<String> tags;

    private String thumbnailKey;

}
