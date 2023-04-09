package com.hybe.larva.entity.locale_code;

import com.hybe.larva.dto.locale_code.LocaleCodeUpdateRequest;
import com.hybe.larva.entity.common.BaseEntity;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Optional;

@ToString(callSuper = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Document(collection = "localeCodes")
public class LocaleCodes extends BaseEntity {

    private String code;

    private String comment;

    @Builder
    public LocaleCodes(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    public LocaleCodes update(LocaleCodeUpdateRequest request) {
        Optional.ofNullable(request.getCode()).ifPresent(v -> this.code = v);
        Optional.ofNullable(request.getComment()).ifPresent(v -> this.comment = v);

        return this;
    }

    public LocaleCodes delete() {
        this.deleted = true;
        return this;
    }
}
