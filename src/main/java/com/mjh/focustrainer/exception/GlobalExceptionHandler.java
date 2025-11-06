package com.mjh.focustrainer.exception;


import com.mjh.focustrainer.common.response.ApiResponse;
import com.mjh.focustrainer.common.response.ErrorCode;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //이메일 전송 오류
    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<ApiResponse<Void>> handlerMailError(MessagingException e) {
        log.error("[Mail Error] {}",e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(
                        ErrorCode.MAIL_SEND_FAILED.getCode(),
                        ErrorCode.MAIL_SEND_FAILED.getMessage()
                ));
    }

    /** 유효성 검사 실패 (ex: @Valid 어노테이션 위반) */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationError(MethodArgumentNotValidException e) {
        log.warn("[Validation Error] {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(
                        ErrorCode.INVALID_REQUEST.getCode(),
                        ErrorCode.INVALID_REQUEST.getMessage()
                ));
    }

    /** 잘못된 인자 (ex: 인증번호 불일치 등) */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("[Business Error] {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(
                        ErrorCode.VERIFICATION_FAILED.getCode(),
                        e.getMessage()
                ));
    }

    /** 처리되지 않은 모든 예외 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception e) {
        log.error("[Unhandled Exception]", e);
        return ResponseEntity.internalServerError()
                .body(ApiResponse.fail(
                        ErrorCode.INTERNAL_ERROR.getCode(),
                        ErrorCode.INTERNAL_ERROR.getMessage()
                ));
    }

}
