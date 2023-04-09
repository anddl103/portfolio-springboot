package com.hybe.larva.dto.banner;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import com.hybe.larva.entity.banner.Banner;
import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.util.OffsetBasedPageRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ToString(callSuper = true)
@Getter
public class BannerSearchRequest extends BaseSearchOffsetRequest {

    private final String artistId;

    @Builder
    public BannerSearchRequest(Integer offset, Integer limit, String artistId) {
        super(offset, limit);
        this.artistId = artistId;
    }

    public Pageable getPageable() {
        // sort descending order by default
        Sort sort = Sort.by(Sort.Order.asc(Banner.SORT_ORDER));
        return new OffsetBasedPageRequest(offset, limit, sort);
    }
}
