package com.hybe.larva.dto.user_info;

import com.hybe.larva.dto.common.BaseSearchRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class UserInfoCardSearchRequest extends BaseSearchRequest {

    private final String albumId;

    private final String artistId;

    private final Boolean favorite;

    private final String keyword;

    @Builder
    public UserInfoCardSearchRequest(LocalDateTime from, LocalDateTime to, Integer page, Integer pageSize,
                                     String albumId, String artistId, Boolean favorite, String keyword) {
        super(from, to, page, pageSize);
        this.albumId = albumId;
        this.artistId = artistId;
        this.favorite = favorite;
        this.keyword = keyword;
    }
}
