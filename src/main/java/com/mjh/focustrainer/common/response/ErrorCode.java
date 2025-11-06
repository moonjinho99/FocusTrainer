package com.mjh.focustrainer.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 인증 관련 2000번대
    MAIL_SEND_FAILED("2000", "메일 발송 중 오류가 발생했습니다."),
    VERIFICATION_FAILED("2001", "인증번호가 일치하지 않습니다."),
    EMAIL_ALREADY_EXISTS("2002", "이미 등록된 이메일입니다."),

    // 공통 오류 1000번대
    INVALID_REQUEST("1000", "요청 파라미터가 올바르지 않습니다."),
    INTERNAL_ERROR("1001", "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final String message;

}
