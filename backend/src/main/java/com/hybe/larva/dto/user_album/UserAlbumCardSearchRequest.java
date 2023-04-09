package com.hybe.larva.dto.user_album;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import com.hybe.larva.entity.view_user_card.ViewUserCard;
import com.hybe.larva.util.OffsetBasedPageRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ToString(callSuper = true)
@Getter
public class UserAlbumCardSearchRequest extends BaseSearchOffsetRequest {


    private final String userAlbumId;

    @Builder
    public UserAlbumCardSearchRequest(Integer offset, Integer limit, String userAlbumId) {
        super(offset, limit);
        this.userAlbumId = userAlbumId;
    }

    public Pageable getPageable() {
        // sort descending order by default
        Sort sort = Sort.by(Sort.Order.asc(ViewUserCard.SORT_POSITION));

        return new OffsetBasedPageRequest(offset, limit, sort);
    }
}
