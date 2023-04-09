package com.hybe.larva.dto.product_key;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class ProductKeySearchRequest extends BaseSearchOffsetRequest {

    private String uid;

    @Builder
    public ProductKeySearchRequest(Integer offset, Integer limit, String uId) {
        super(offset, limit);

        this.uid = uId;
    }
}
