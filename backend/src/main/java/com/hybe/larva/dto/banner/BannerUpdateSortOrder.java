package com.hybe.larva.dto.banner;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class BannerUpdateSortOrder {
    private String bannerId;
    private int sortOrder;
}
