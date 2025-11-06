package com.mjh.focustrainer.auth.controller;


import com.mjh.focustrainer.auth.dto.CodeRequest;
import com.mjh.focustrainer.auth.dto.EmailRequest;
import com.mjh.focustrainer.auth.dto.SignupRequest;
import com.mjh.focustrainer.auth.serivce.AuthService;
import com.mjh.focustrainer.auth.serivce.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Auth", description = "인증 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "이메일 인증번호 발송", description = "입력한 이메일로 인증번호를 발송합니다.")

    @PostMapping("/mail/send")
    public ResponseEntity<?> sendMail(@Valid @RequestBody EmailRequest request) throws MessagingException
    {
        String email = request.getEmail();
        return authService.sendMail(email);
    }

    @Operation(summary = "이메일 인증번호 검증", description = "입력한 인증번호가 일치하는지 확인합니다.")
    @PostMapping("/code/check")
    public ResponseEntity<?> codeCheck(@Valid @RequestBody CodeRequest request)
    {
        String email = request.getEmail();
        String code = request.getCode();
        return authService.codeCheck(email,code);
    }

}
