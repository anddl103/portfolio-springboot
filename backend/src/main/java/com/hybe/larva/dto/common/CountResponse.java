package com.hybe.larva.dto.common;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CountResponse {

    private final long count;

    public CountResponse(long count) {
        this.count = count;
    }
}
