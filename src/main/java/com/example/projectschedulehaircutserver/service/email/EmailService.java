package com.example.projectschedulehaircutserver.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.email.sender.name}")
    private String senderName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendBookingConfirmation(String toEmail, String customerName, String bookingDetails, String employeeName) {
        String subject = "📅 Xác nhận lịch hẹn tại Salon";
        String body = buildConfirmationTemplate(customerName, bookingDetails, employeeName);
        send(toEmail, subject, body);
    }

    @Async
    public void sendBookingCancellation(String toEmail, String customerName, String bookingDetails, String employeeName, String reason) {
        String subject = "❌ Thông báo hủy lịch hẹn";
        String body = buildCancellationTemplate(customerName, bookingDetails, employeeName, reason);
        send(toEmail, subject, body);
    }

    private void send(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, senderName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            logger.info("Email sent to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Gửi email thất bại", e);
        }
    }

    private String buildConfirmationTemplate(String customerName, String bookingDetails, String employeeName) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "  body { font-family: 'Arial', sans-serif; line-height: 1.6; color: #333; }" +
                "  .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "  .header { background-color: #4CAF50; color: white; padding: 15px; text-align: center; }" +
                "  .content { padding: 20px; background-color: #f9f9f9; }" +
                "  .footer { margin-top: 20px; font-size: 0.8em; color: #777; text-align: center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h2>XÁC NHẬN LỊCH HẸN</h2>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Xin chào <strong>" + customerName + "</strong>,</p>" +
                "<p>Nhân viên <strong>" + employeeName + "</strong> đã xác nhận lịch hẹn của bạn với thông tin sau:</p>" +
                bookingDetails +
                "<p>Vui lòng đến đúng giờ hẹn. Nếu có thay đổi, xin vui lòng liên hệ salon trước ít nhất 2 giờ.</p>" +
                "<p>Trân trọng,<br><strong>Đội ngũ Salon</strong></p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Đây là email tự động. Vui lòng không trả lời.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildCancellationTemplate(String customerName, String bookingDetails, String employeeName, String reason) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "  body { font-family: 'Arial', sans-serif; line-height: 1.6; color: #333; }" +
                "  .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "  .header { background-color: #f44336; color: white; padding: 15px; text-align: center; }" +
                "  .content { padding: 20px; background-color: #f9f9f9; }" +
                "  .reason-box { background-color: #ffe6e6; padding: 10px; border-left: 4px solid #f44336; margin: 15px 0; }" +
                "  .footer { margin-top: 20px; font-size: 0.8em; color: #777; text-align: center; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h2>THÔNG BÁO HỦY LỊCH HẸN</h2>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Xin chào <strong>" + customerName + "</strong>,</p>" +
                "<p>Chúng tôi rất tiếc phải thông báo rằng nhân viên <strong>" + employeeName + "</strong> đã hủy lịch hẹn sau:</p>" +
                bookingDetails +
                "<div class='reason-box'>" +
                "<p><strong>Lý do:</strong><br>" + (reason != null ? reason : "Không có lý do cụ thể") + "</p>" +
                "</div>" +
                "<p>Xin lỗi vì sự bất tiện này. Vui lòng đặt lịch lại hoặc liên hệ với chúng tôi để được hỗ trợ.</p>" +
                "<p>Trân trọng,<br><strong>Đội ngũ Salon</strong></p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Đây là email tự động. Vui lòng không trả lời.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String customerName, String verificationCode) {
        String subject = "🔐 Yêu cầu đổi mật khẩu - Mã xác thực";
        String body = buildPasswordResetEmailTemplate(customerName, verificationCode);
        send(toEmail, subject, body);
    }

    private String buildPasswordResetEmailTemplate(String customerName, String verificationCode) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "  body { font-family: 'Arial', sans-serif; line-height: 1.6; color: #333; }" +
                "  .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "  .header { background-color: #4285F4; color: white; padding: 15px; text-align: center; }" +
                "  .content { padding: 20px; background-color: #f9f9f9; }" +
                "  .code { font-size: 24px; font-weight: bold; color: #4285F4; text-align: center; margin: 20px 0; }" +
                "  .footer { margin-top: 20px; font-size: 0.8em; color: #777; text-align: center; }" +
                "  .note { color: #d32f2f; font-style: italic; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h2>YÊU CẦU ĐỔI MẬT KHẨU</h2>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Xin chào <strong>" + customerName + "</strong>,</p>" +
                "<p>Bạn đã yêu cầu đổi mật khẩu. Vui lòng sử dụng mã xác thực sau:</p>" +
                "<div class='code'>" + verificationCode + "</div>" +
                "<p class='note'>Mã có hiệu lực trong 10 phút. Không chia sẻ mã này với bất kỳ ai.</p>" +
                "<p>Nếu bạn không yêu cầu đổi mật khẩu, vui lòng bỏ qua email này.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Đây là email tự động. Vui lòng không trả lời.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}