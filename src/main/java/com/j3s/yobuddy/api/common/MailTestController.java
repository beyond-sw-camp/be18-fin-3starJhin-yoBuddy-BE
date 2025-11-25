package com.j3s.yobuddy.api.common;

import com.j3s.yobuddy.common.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailTestController {

    private final EmailService emailService;

    @GetMapping("/test/mail")
    public String testMail() {
        emailService.send(
            "yobuddy390@gmail.com",
            "테스트 메일",
            "YoBuddy Gmail SMTP 정상 작동!"
        );
        return "OK";
    }
}
