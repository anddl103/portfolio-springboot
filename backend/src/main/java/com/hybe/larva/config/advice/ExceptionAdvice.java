package com.hybe.larva.config.advice;

import com.hybe.larva.dto.common.CommonResult;
import com.hybe.larva.dto.common.ResponseHelper;
import com.hybe.larva.enums.ErrorCode;
import com.hybe.larva.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleDefaultException(HttpServletRequest request, Exception e) {
        log.error(e.getMessage(), e);
        return ResponseHelper.newFailureResult(ErrorCode.UNKNOWN, e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleAccessDeniedException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.ACCESS_DENIED, e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleHttpRequestMethodNotSupportedException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.INVALID_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleMethodArgumentNotValidException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.INVALID_REQUEST, e.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleDuplicateKeyException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.DUPLICATED_KEY, e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleResourceNotFoundException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.NO_RESOURCE, e.getMessage());
    }

    @ExceptionHandler(InvalidContentTypeException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleInvalidContentTypeException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.INVALID_CONTENT_TYPE, e.getMessage());
    }

    @ExceptionHandler(InsufficientProductKeyException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleInsufficientSerialException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.INSUFFICIENT_PRODUCT_KEY, e.getMessage());
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleAuthException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(DownloadLimitException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleDownloadLimitException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.DOWNLOAD_LIMIT, e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleIllegalStateException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.ILLEGAL_STATE, e.getMessage());
    }

    @ExceptionHandler(InvalidProductKeyException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleInvalidProductKeyException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.INVALID_SERIAL, e.getMessage());
    }

    @ExceptionHandler(InvalidProductOrderException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleInvalidProductOrderException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.INVALID_ORDER, e.getMessage());
    }



    @ExceptionHandler(S3FileUploadException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleS3FileUploadException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.UPLOAD_SIZE_LIMIT, e.getMessage());
    }

    @ExceptionHandler(AccessTokenExpiredException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleAccessTokenExpiredException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.ACCESS_TOKEN_EXPIRED, e.getMessage());
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleRefreshTokenExpiredException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.REFRESH_TOKEN_EXPIRED, e.getMessage());
    }

    @ExceptionHandler(AssignedProductKeyException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleAssignedProductKeyExceptionException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.NOT_ASSIGNED_PRODUCT_KEY, e.getMessage());
    }

    @ExceptionHandler(TaggedProductKeyException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleTaggedProductKeyExceptionException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.NOT_TAGGED_PRODUCT_KEY, e.getMessage());
    }

    @ExceptionHandler(VerifiedProductKeyException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleVerifiedProductKeyExceptionException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.NOT_VERIFIED_PRODUCT_KEY, e.getMessage());
    }

    @ExceptionHandler(DeletedResourceException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleDeletedResourceException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.DELETED_RESOURCE, e.getMessage());
    }

    @ExceptionHandler(ProtectedResourceException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleProtectedResourceException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.PROTECTED_RESOURCE, e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleNullPointerException(HttpServletRequest request, Exception e) {
        log.error("NullPointerException", e);
        return ResponseHelper.newFailureResult(ErrorCode.INTERNAL_ERROR, e.getMessage());
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    @ResponseStatus(HttpStatus.OK)
    protected CommonResult handleOptimisticLockingFailureException(HttpServletRequest request, Exception e) {
        return ResponseHelper.newFailureResult(ErrorCode.OPTIMISTIC_LOCKING_FAILURE, e.getMessage());
    }
}
