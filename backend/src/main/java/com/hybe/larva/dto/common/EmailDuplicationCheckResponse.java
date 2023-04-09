package com.hybe.larva.dto.common;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class EmailDuplicationCheckResponse {

    private final boolean duplicated;
    private final boolean disabled;

    public EmailDuplicationCheckResponse(boolean duplicated, boolean disabled) {
        this.duplicated = duplicated;
        this.disabled = disabled;
    }
}
