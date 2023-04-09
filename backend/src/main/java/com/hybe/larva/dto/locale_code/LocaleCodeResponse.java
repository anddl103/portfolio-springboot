package com.hybe.larva.dto.locale_code;

import com.hybe.larva.dto.common.BaseResponse;
import com.hybe.larva.entity.locale_code.LocaleCodes;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class LocaleCodeResponse extends BaseResponse {

    private String code;

    private String comment;

    public LocaleCodeResponse(LocaleCodes localeCodes) {
        super(localeCodes);

        this.code = localeCodes.getCode();
        this.comment = localeCodes.getComment();
    }
}
