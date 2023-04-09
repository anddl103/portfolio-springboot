package com.hybe.larva.dto.language_pack;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString
@Getter
@Builder
public class LanguagePackUpdateRequest {

    private Map<String, String> values;


    @JsonCreator
    @Builder
    public LanguagePackUpdateRequest(
            @JsonProperty("values") Map<String, String> values
    ) {
        this.values = values;
    }

}
