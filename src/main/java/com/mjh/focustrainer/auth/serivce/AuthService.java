package com.mjh.focustrainer.auth.serivce;

import com.mjh.focustrainer.auth.dto.LoginRequest;
import com.mjh.focustrainer.auth.dto.SignupRequest;
import com.mjh.focustrainer.auth.entity.RefreshToken;
import com.mjh.focustrainer.auth.repository.RefreshTokenRepository;
import com.mjh.focustrainer.common.jwt.JwtProvider;
import com.mjh.focustrainer.user.repository.UserRepository;
import com.mjh.focustrainer.common.response.ApiResponse;
import com.mjh.focustrainer.common.response.ErrorCode;
import com.mjh.focustrainer.common.exception.CustomException;
import com.mjh.focustrainer.user.entity.User;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final MailService mailService;
    private final Map<String, String> codeStorage = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public ResponseEntity<ApiResponse<Void>> sendMail(String email) throws MessagingException
    {
        String code = mailService.sendVerificationMail(email);
        codeStorage.put(email,code);
        return ResponseEntity.ok(ApiResponse.ok("메일 발송 완료"));
    }

    public ResponseEntity<ApiResponse<Boolean>> codeCheck(String email,String code)
    {
        if(codeStorage.get(email).equals(code))
        {
            return ResponseEntity.ok(ApiResponse.ok("인증번호 검증 완료", true));
        }else{
            throw new IllegalArgumentException("인증번호 검증 실패");
        }
    }

    public void signup(SignupRequest request)
    {
        // 이메일 중복 검사
        if(userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .nickname(request.getNickname())
                .build();

        userRepository.save(user);
    }

    public ApiResponse<Map<String,String>> login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getEmail());

        refreshTokenRepository.deleteByUser(user);

        RefreshToken newToken = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expireTime(
                        jwtProvider.getExpiration(refreshToken)
                                .toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDateTime()
                ).build();

        refreshTokenRepository.save(newToken);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        tokens.put("nickname",user.getNickname());

        return ApiResponse.ok("로그인 성공", tokens);
    }

    public void logout(HttpServletRequest request)
    {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String token = header.substring(7);
        Long userId = jwtProvider.getUserId(token);

        refreshTokenRepository.deleteByUserId(userId);
    }
}
