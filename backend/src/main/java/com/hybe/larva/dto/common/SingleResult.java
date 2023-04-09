package com.hybe.larva.dto.common;

import com.hybe.larva.enums.ErrorCode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class SingleResult<T> extends CommonResult {

    @NotNull
    private T data;

    public SingleResult(ErrorCode code, String message, T data) {
        super(code, message);
        this.data = data;
    }
}
