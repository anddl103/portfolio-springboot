package com.hybe.larva.dto.common;

import com.hybe.larva.enums.ErrorCode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class ResponseHelper {

    public static CommonResult newSuccessResult() {
        return new CommonResult(ErrorCode.NONE, "success");
    }

    public static CommonResult newFailureResult(ErrorCode code, String message) {
        return new CommonResult(code, message);
    }

    public static <T> SingleResult<T> newSingleResult(ErrorCode code, String message, T data) {
        return new SingleResult<>(code, message, data);
    }

    public static <T> SingleResult<T> newSingleSuccessResult(T data) {
        return new SingleResult<>(ErrorCode.NONE, "success", data);
    }

    public static <T> ListResult<T> newListResult(ErrorCode code, String message, List<T> list) {
        return new ListResult<>(code, message, list);
    }

    public static <T> ListResult<T> newListSuccessResult(List<T> list) {
        return newListSuccessResult(list, null);
    }

    public static <T> ListResult<T> newListSuccessResult(List<T> list, ListResult.PageInfo pageInfo) {
        return new ListResult<>(ErrorCode.NONE, "success", list, pageInfo);
    }

    public static <T> ResponseEntity<T> newContentsSuccessRedirect(String uri) throws URISyntaxException {
        URI redirectUri = new URI(uri);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(redirectUri);
        return new ResponseEntity<>(httpHeaders, HttpStatus.TEMPORARY_REDIRECT);
    }
}
