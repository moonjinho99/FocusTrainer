package com.mjh.focustrainer.auth.controller;


import com.mjh.focustrainer.auth.dto.CodeRequest;
import com.mjh.focustrainer.auth.dto.EmailRequest;
import com.mjh.focustrainer.auth.dto.LoginRequest;
import com.mjh.focustrainer.auth.dto.SignupRequest;
import com.mjh.focustrainer.auth.serivce.AuthService;
import com.mjh.focustrainer.auth.serivce.MailService;
import com.mjh.focustrainer.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @Operation(summary = "회원가입", description = "사용자의 닉네임, 이메일로 회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest request)
    {
        authService.signup(request);
        return ResponseEntity.ok(ApiResponse.ok("회원가입이 완료되었습니다."));
    }


    @Operation(summary = "로그인", description = "로그인을 진행합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String,String>>> login(@Valid @RequestBody LoginRequest request)
    {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 진행합니다.")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request)
    {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.ok("로그아웃 합니다."));
    }
}
