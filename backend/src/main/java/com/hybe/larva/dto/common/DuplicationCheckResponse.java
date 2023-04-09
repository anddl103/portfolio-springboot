package com.hybe.larva.dto.common;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class DuplicationCheckResponse {

    private final boolean duplicated;

    public DuplicationCheckResponse(boolean duplicated) {
        this.duplicated = duplicated;
    }
}
