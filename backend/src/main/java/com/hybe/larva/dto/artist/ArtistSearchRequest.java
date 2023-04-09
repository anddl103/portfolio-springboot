package com.hybe.larva.dto.artist;

import com.hybe.larva.dto.common.BaseSearchOffsetRequest;
import com.hybe.larva.entity.artist.Artist;
import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.util.OffsetBasedPageRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Getter
public class ArtistSearchRequest extends BaseSearchOffsetRequest {

    private final String name;

    private final Boolean display;

    @Builder
    public ArtistSearchRequest(LocalDateTime from, LocalDateTime to, Integer offset, Integer limit, String name, Boolean display) {
        super(from, to, offset, limit);
        this.name = name;
        this.display = display;
    }

    public Pageable getPageable() {
        // sort descending order by default
        Sort sort = Sort.by(Sort.Order.asc(Artist.SORT_ORDER));
        return new OffsetBasedPageRequest(offset, limit, sort);
    }
}
