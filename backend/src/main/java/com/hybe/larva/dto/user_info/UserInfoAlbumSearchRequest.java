package com.hybe.larva.dto.user_info;

import com.hybe.larva.dto.common.BaseSearchRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class UserInfoAlbumSearchRequest extends BaseSearchRequest {

    private final String artistId;

    private final String keyword;

    @Builder
    public UserInfoAlbumSearchRequest(LocalDateTime from, LocalDateTime to, Integer page, Integer pageSize,
                                      String artistId, String keyword) {
        super(from, to, page, pageSize);
        this.artistId = artistId;
        this.keyword = keyword;
    }
}
