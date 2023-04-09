package com.hybe.larva.dto.language_pack;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.language_pack.LanguagePack;
import com.hybe.larva.enums.Usage;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString(callSuper = true)
@Getter
public class LanguagePackResponse extends BaseResponse {

    private final Map<String, String> values;

    public LanguagePackResponse(LanguagePack languagePack) {
        super(languagePack);

        this.values = languagePack.getValues();
    }
}
