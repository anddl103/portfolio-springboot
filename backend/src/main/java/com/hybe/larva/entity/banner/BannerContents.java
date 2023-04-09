package com.hybe.larva.entity.banner;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Builder
public class BannerContents {

    private String image;

    private String link;
}
