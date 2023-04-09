package com.hybe.larva.dto.common;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class DemoRequest {

    private String artistId;

    private String uid;

    private int percent;
}
