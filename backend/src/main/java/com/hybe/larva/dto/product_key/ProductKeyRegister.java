package com.hybe.larva.dto.product_key;

import lombok.Data;
import lombok.ToString;

@ToString(callSuper = true)
@Data
public class ProductKeyRegister {

    private String userAlbumId;

    private String userArtistId;
}
