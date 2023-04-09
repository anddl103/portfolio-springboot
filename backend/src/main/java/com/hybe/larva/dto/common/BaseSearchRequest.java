package com.hybe.larva.dto.common;

import com.hybe.larva.entity.common.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
public class BaseSearchRequest {

    // as of creation time
    protected LocalDateTime from;

    // as of creation time
    protected LocalDateTime to;

    // page number (from 0)
    protected Integer page;

    // page size
    protected Integer pageSize;

    public BaseSearchRequest(LocalDateTime from, LocalDateTime to, Integer page, Integer pageSize) {
        this.from = from;
        this.to = to;
        this.page = page;
        this.pageSize = pageSize == -1 ? Integer.MAX_VALUE : pageSize;
    }

    public Pageable getPageable() {
        // sort descending order by default
        Sort sort = Sort.by(Sort.Order.desc(BaseEntity.CREATED_AT));
        return PageRequest.of(page, pageSize, sort);
        /*
        offet 처리 시
         */
        //return new OffsetBasedPageRequest(page, pageSize, sort);
    }
}
