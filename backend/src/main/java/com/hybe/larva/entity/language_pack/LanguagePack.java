package com.hybe.larva.entity.language_pack;

import com.hybe.larva.dto.language_pack.LanguagePackUpdateRequest;
import com.hybe.larva.entity.common.BaseEntity;
import com.hybe.larva.enums.Usage;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Optional;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "languagePacks")
public class LanguagePack extends BaseEntity {

    public static final String VALUES_KO = "values.ko";

    /**
     * contry_code : {
     *     key : text
     * }
     */
    private Map<String, String> values;


    @Builder
    public LanguagePack(Map<String, String> values) {
        this.values = values;
    }

    public LanguagePack update(LanguagePackUpdateRequest request) {
        Optional.ofNullable(request.getValues()).ifPresent(v -> this.values = v);
        return this;
    }
}
