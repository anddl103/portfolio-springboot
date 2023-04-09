package com.hybe.larva.dto.common;

import com.hybe.larva.enums.ContentsType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;

@ToString
@Data
@Builder
public class CardContents {

    private String title;

    private String description;

    private String thumbnailKey;

    @ApiModelProperty(dataType = "java.lang.String")
    private ContentsType type;

    private String imageKey;

    private String videoKey;

    public List<String> getFiles () {
        return Arrays.asList(thumbnailKey, imageKey, videoKey);
    }
}
