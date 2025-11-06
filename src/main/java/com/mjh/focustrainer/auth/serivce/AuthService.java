package com.mjh.focustrainer.auth.serivce;

import com.mjh.focustrainer.auth.dto.SignupRequest;
import com.mjh.focustrainer.auth.repository.UserRepository;
import com.mjh.focustrainer.common.response.ApiResponse;
import com.mjh.focustrainer.common.response.ErrorCode;
import com.mjh.focustrainer.exception.CustomException;
import com.mjh.focustrainer.user.entity.User;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MailService mailService;
    private final Map<String, String> codeStorage = new ConcurrentHashMap<>();
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
}
