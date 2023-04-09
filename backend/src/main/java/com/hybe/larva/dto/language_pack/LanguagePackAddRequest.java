package com.hybe.larva.dto.language_pack;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hybe.larva.entity.language_pack.LanguagePack;
import lombok.*;

import java.util.Map;

@ToString
@Getter
@Builder
public class LanguagePackAddRequest {

    /**
     * localeCode : text
     */
    private Map<String, String> values;

    public LanguagePack toEntity() {
        return LanguagePack.builder()
                .values(values)
                .build();
    }


    @JsonCreator
    @Builder
    public LanguagePackAddRequest(
            @JsonProperty("values") Map<String, String> values
    ) {
        this.values = values;
    }

}
