package com.hybe.larva.dto.common;

import com.hybe.larva.entity.common.BaseEntity;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@ToString
@Getter
public class BaseResponse {

    // Entity id
    @NotBlank
    protected final String id;

    // Created date-time
    @NotNull
    protected final LocalDateTime createdAt;

    // Creator: User.id
    @NotBlank
    protected final String createdBy;

    protected BaseResponse(BaseEntity entity) {
        this.id = entity.getId();
        this.createdAt = entity.getCreatedAt();
        this.createdBy = entity.getCreatedBy();
    }
}
