package com.mjh.focustrainer.auth.serivce;

import com.mjh.focustrainer.common.response.ApiResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MailService mailService;
    private final Map<String, String> codeStorage = new ConcurrentHashMap<>();


    public ResponseEntity<ApiResponse<Void>> sendMail(String email) throws MessagingException
    {
        String code = mailService.sendVerificationMail(email);
        codeStorage.put(email,code);
        return ResponseEntity.ok(ApiResponse.ok("메일 발송 완료"));
    }

    public ResponseEntity<ApiResponse<Boolean>> codeCheck(String email,String code) throws MessagingException
    {
        if(codeStorage.get(email).equals(code))
        {
            return ResponseEntity.ok(ApiResponse.ok("인증번호 검증 완료", true));
        }

        return ResponseEntity.ok(ApiResponse.ok("인증번호 검증 실패", false));
    }


}
