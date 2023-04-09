package com.hybe.larva.dto.album_working;

import com.hybe.larva.enums.BatchState;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class BatchJobEnabledResponse {

    private BatchState batchState;

    public BatchJobEnabledResponse(BatchState batchState) {
        this.batchState = batchState;
    }
}
