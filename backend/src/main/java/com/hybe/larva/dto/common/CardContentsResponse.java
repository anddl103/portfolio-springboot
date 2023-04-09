package com.hybe.larva.dto.common;

import com.hybe.larva.enums.ContentsType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@ToString
@Data
@Builder
public class CardContentsResponse {

    private Map<String, Object> title;

    private Map<String, Object> description;

    private String thumbnailKey;

    @ApiModelProperty(dataType = "java.lang.String")
    private ContentsType type;

    private String imageKey;

    private String videoKey;
}
