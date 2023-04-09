package com.hybe.larva.dto.artist;

import com.hybe.larva.dto.common.BaseSearchRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class ArtistSearchPageRequest extends BaseSearchRequest {

    private final String name;

    @Builder
    public ArtistSearchPageRequest(LocalDateTime from, LocalDateTime to, Integer page, Integer pageSize, String name) {
        super(from, to, page, pageSize);
        this.name = name;
    }
}
