package com.j3s.yobuddy.common.mail;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void send(String to, String subject, String contentHtml) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);

            String fullHtml = wrapWithTemplate(contentHtml, subject);
            helper.setText(fullHtml, true);

            mailSender.send(msg);

        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    private String wrapWithTemplate(String body, String title) {

        return """
        <!DOCTYPE html>
        <html lang="ko">
        <head>
            <meta charset="UTF-8">
            <title>YoBuddy 메일</title>
        </head>

        <body style="margin:0; padding:0; background:#f3f5fb; 
                     font-family:-apple-system, BlinkMacSystemFont, 'Segoe UI', 
                     Roboto, 'Noto Sans KR', Arial, sans-serif;">

          <table width="100%" cellpadding="0" cellspacing="0" 
                 style="background:#f3f5fb; padding:30px 0;">
            <tr>
              <td align="center">

                <table width="520" cellpadding="0" cellspacing="0"
                       style="background:#ffffff; border-radius:16px;
                              padding:32px; box-shadow:0 8px 25px rgba(0,0,0,0.06);">

                  <!-- HEADER -->
                  <tr>
                    <td style="padding-bottom:20px; border-bottom:1px solid #e6e9f4;">
                      <div style="font-size:22px; font-weight:800; color:#294594;">
                        YoBuddy
                      </div>
                      <div style="margin-top:6px; font-size:13px; color:#8a90a8;">
                        당신의 온보딩 파트너
                      </div>
                    </td>
                  </tr>

                  <!-- TITLE -->
                  <tr>
                    <td style="padding-top:24px; padding-bottom:6px;">
                      <div style="font-size:18px; font-weight:700; color:#222;">
                        """ + title + """
                      </div>
                    </td>
                  </tr>

                  <!-- BODY -->
                  <tr>
                    <td style="padding-top:4px; padding-bottom:22px;">
                      <div style="font-size:14px; line-height:1.8; color:#444;">
                        """ + body + """
                      </div>
                    </td>
                  </tr>

                  <!-- FOOTER -->
                  <tr>
                    <td style="padding-top:20px; border-top:1px solid #e6e9f4;">
                      <div style="margin-top:10px; font-size:11px; color:#a0a4b8; line-height:1.6;">
                        이 메일은 YoBuddy 시스템에서 자동 발송되었습니다.<br>
                        © 2025 YoBuddy. All rights reserved.
                      </div>
                    </td>
                  </tr>

                </table>

              </td>
            </tr>
          </table>

        </body>
        </html>
        """;
    }
}
