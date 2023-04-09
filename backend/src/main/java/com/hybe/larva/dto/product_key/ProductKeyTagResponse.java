package com.hybe.larva.dto.product_key;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.product_key.ProductKey;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class ProductKeyTagResponse extends BaseResponse {

    private final String code;

    private final String productOrderId;

    private final String nfcCardId;

    private final boolean assigned;

    private final boolean verified;

    private final boolean tagged;

    private final boolean duplicated;

    private String userAlbumId;

    private String userArtistId;

    public ProductKeyTagResponse(ProductKey productKey, String code, boolean duplicated,
                                 ProductKeyRegister register) {
        super(productKey);

        if (code != null) {
            this.code = code;
        } else {
            this.code = productKey.getCode();
        }

        this.productOrderId = productKey.getProductOrderId();
        this.nfcCardId = productKey.getNfcCardId();
        this.assigned = productKey.isAssigned();
        this.verified = productKey.isVerified();
        this.tagged = productKey.isTagged();
        this.duplicated = duplicated;

        if (register != null) {
            this.userAlbumId = register.getUserAlbumId();
            this.userArtistId = register.getUserArtistId();
        }
    }
}
