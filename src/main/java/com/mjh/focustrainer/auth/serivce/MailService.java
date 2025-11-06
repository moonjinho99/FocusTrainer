package com.mjh.focustrainer.auth.serivce;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public String sendVerificationMail(String to) throws MessagingException {

        String code = String.format("%06d",new Random().nextInt(999999));

        String subject = "[FocusTrainer] 이메일 인증번호";
        String content = """
                <h3>FocusTrainer 이메일 인증</h3>
                <p>아래 인증번호를 입력창에 입력해주세요.</p>
                <h2 style="color:#4ca1af;">%s</h2>
                """.formatted(code);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);

        // 프론트에서 비교용으로 반환
        return code;
    }

}
