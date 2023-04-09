package com.hybe.larva.dto.common;

import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.util.OffsetBasedPageRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
public class BaseSearchOffsetRequest {

    // page number (from 0)
    protected Integer offset;

    // page size
    protected Integer limit;

    // as of creation time
    protected LocalDateTime from;

    // as of creation time
    protected LocalDateTime to;


    public BaseSearchOffsetRequest(Integer offset, Integer limit) {
        this.offset = offset;
        this.limit = limit == -1 ? Integer.MAX_VALUE : limit;
    }

    public BaseSearchOffsetRequest(LocalDateTime from, LocalDateTime to, Integer offset, Integer limit) {
        this.from = from;
        this.to = to;
        this.offset = offset;
        this.limit = limit == -1 ? Integer.MAX_VALUE : limit;
    }

    public Pageable getPageable() {
        // sort descending order by default
        Sort sort = Sort.by(Sort.Order.desc(BaseEntity.CREATED_AT));




        return new OffsetBasedPageRequest(offset, limit, sort);
    }
}
