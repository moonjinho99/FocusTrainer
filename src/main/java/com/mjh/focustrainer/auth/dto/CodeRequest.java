package com.mjh.focustrainer.auth.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CodeRequest {

    @Schema(description = "회원 이메일", example = "test@naver.com")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    private String email;

    @Schema(description = "인증번호", example = "987654")
    @NotBlank(message = "인증번호는 필수 입력값입니다.")
    private String code;
}
