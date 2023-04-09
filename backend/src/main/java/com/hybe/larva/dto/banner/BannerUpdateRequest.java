package com.hybe.larva.dto.banner;

import com.hybe.larva.entity.banner.BannerContents;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@ToString
@Getter
@Builder
public class BannerUpdateRequest {

    private List<String> tags;

    private String title;

    private String artistId;

    private String albumId;

    private String thumbnailKey;

    // Map<localeCode, <link, imageUrl>>
    private Map<String, BannerContents> contents;

    @Setter
    private Integer sortOrder;

    private boolean display;

}
