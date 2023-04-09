package com.hybe.larva.dto.album_working;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Data
@RequiredArgsConstructor
public class BatchJobEnabledUpdateRequest {

    private boolean jobEnabled;
}
