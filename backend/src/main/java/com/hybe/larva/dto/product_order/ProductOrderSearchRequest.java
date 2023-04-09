package com.hybe.larva.dto.product_order;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import com.hybe.larva.dto.common.BaseSearchRequest;
import com.hybe.larva.enums.ProductOrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class ProductOrderSearchRequest extends BaseSearchOffsetRequest {

    private String artistId;

    private ProductOrderStatus productOrderStatus;

    @Builder
    public ProductOrderSearchRequest(LocalDateTime from, LocalDateTime to, Integer offset, Integer limit, String artistId,
                                     ProductOrderStatus productOrderStatus) {
        super(from, to, offset, limit);

        this.artistId = artistId;
        this.productOrderStatus = productOrderStatus;
    }
}
