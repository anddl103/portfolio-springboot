package com.hybe.larva.dto.common;

import com.hybe.larva.enums.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CommonResult {

    // 0: success, !0: error
    @NotNull
    private int code;

    // error message
    @NotNull
    private String message;

    public CommonResult(ErrorCode code, String message) {
        this.code = code.getCode();
        this.message = message;
    }
}
