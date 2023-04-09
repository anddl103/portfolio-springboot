package com.hybe.larva.dto.common;

import com.hybe.larva.enums.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ListResult<T> extends CommonResult {

    @NotNull
    private List<T> data;

    private PageInfo pageInfo;

    public ListResult(ErrorCode code, String message, List<T> data) {
        this(code, message, data, null);
    }

    public ListResult(ErrorCode code, String message, List<T> data, PageInfo pageInfo) {
        super(code, message);
        this.data = data;
        this.pageInfo = pageInfo;
    }

    @Getter
    @Builder
    public static class PageInfo {
        @NotNull
        @Min(0)
        private final Integer pageNumber;

        @NotNull
        @Min(0)
        private final Integer pageSize;

        @NotNull
        @Min(0)
        private final Long offset;

        @NotNull
        @Min(0)
        private final Integer totalPages;

        @NotNull
        @Min(0)
        private final Long totalElements;

        @NotNull
        private final Boolean isFirst;

        @NotNull
        private final Boolean isLast;

        public static PageInfo of(Page<?> page) {
            Pageable pageable = page.getPageable();
            return PageInfo.builder()
                    .pageNumber(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .offset(pageable.getOffset())
                    .totalPages(page.getTotalPages())
                    .totalElements(page.getTotalElements())
                    .isFirst(page.isFirst())
                    .isLast(page.isLast())
                    .build();
        }
    }
}
