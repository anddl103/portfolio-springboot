package com.hybe.larva.dto.user_album;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.entity.user_album.UserAlbum;
import com.hybe.larva.util.OffsetBasedPageRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ToString(callSuper = true)
@Getter
public class UserAlbumSearchRequest extends BaseSearchOffsetRequest {


    private final String artistId;

    @Builder
    public UserAlbumSearchRequest(Integer offset, Integer limit, String artistId) {
        super(offset, limit);
        this.artistId = artistId;
    }

    public Pageable getPageable() {
        // sort descending order by default
        Sort sort = Sort.by(Sort.Order.desc(UserAlbum.UPDATED_AT));
        return new OffsetBasedPageRequest(offset, limit, sort);
    }
}
