package com.hybe.larva.entity.common;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@Builder
public class Thumbnails {

    private String openedUrl;

    private String closedUrl;
}
