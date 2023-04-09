package com.hybe.larva.entity.banner;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BannerMaxSortOrder {

    private int maxSortOrder;
}
