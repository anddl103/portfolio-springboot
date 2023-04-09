package com.hybe.larva.dto.banner;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class BannerUpdateSortOrderRequest {

    private List<BannerUpdateSortOrder> orderList;
}
