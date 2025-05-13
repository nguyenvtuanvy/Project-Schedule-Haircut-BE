package com.example.projectschedulehaircutserver.controller.chatbot;

import com.example.projectschedulehaircutserver.service.chatbot.GeminiAIService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/web/chatbot")
@AllArgsConstructor
public class ChatBotController {
    private final GeminiAIService geminiAIService;

    @PostMapping("/ask")
    public ResponseEntity<String> handleChatRequest(@RequestBody Map<String, String> request) {
        String userMessage = request.get("message");
        String response;

        if (userMessage == null || userMessage.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Vui lòng nhập câu hỏi của bạn");
        }

        String lowerMessage = userMessage.toLowerCase();

        if (containsAny(lowerMessage, "kiểu tóc", "khuôn mặt", "tóc", "mặt")) {
            response = handleHairStyleRequest(userMessage);
        } else if (containsAny(lowerMessage, "dịch vụ", "service", "giá", "bảng giá")) {
            response = handleServiceInquiry();
        } else if (containsAny(lowerMessage, "đặt lịch", "book", "hẹn", "lịch")) {
            response = handleBookingRequest(userMessage);
        } else {
            response = handleGeneralQuestion(userMessage);
        }

        return ResponseEntity.ok(response);
    }

    private boolean containsAny(String input, String... keywords) {
        for (String keyword : keywords) {
            if (input.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String handleHairStyleRequest(String userMessage) {
        String prompt = """
            Bạn là chuyên gia tóc tại tiệm BarberPro. Hãy phân tích và trả lời câu hỏi sau:
            
            Câu hỏi: "%s"
            
            Yêu cầu:
            1. Phân tích khuôn mặt và kiểu tóc hiện tại (nếu có thông tin)
            2. Đưa ra 3-5 gợi ý kiểu tóc phù hợp
            3. Mỗi gợi ý gồm: tên kiểu tóc, mô tả ngắn, lý do phù hợp
            4. Kết thúc bằng câu hỏi: "Bạn muốn đặt lịch thử kiểu nào không?"
            
            Trả lời bằng tiếng Việt, thân thiện, chuyên nghiệp.
            """.formatted(userMessage);

        return geminiAIService.generateContent(prompt);
    }

    private String handleServiceInquiry() {
        String prompt = """
            Bạn đang giới thiệu dịch vụ của tiệm BarberPro. Hãy liệt kê theo định dạng:
            
            DANH SÁCH DỊCH VỤ:
            1. Cắt tóc cơ bản - 150.000đ - 30 phút
            2. Cạo mặt - 100.000đ - 20 phút
            3. Nhuộm tóc - 300.000đ - 60 phút
            4. Gội đầu massage - 120.000đ - 25 phút
            5. Tạo kiểu đặc biệt - 250.000đ - 45 phút
            
            Kết thúc bằng câu: "Bạn muốn đặt lịch dịch vụ nào ạ?"
            """;

        return geminiAIService.generateContent(prompt);
    }

    private String handleBookingRequest(String userMessage) {
        String prompt = """
            Bạn đang hỗ trợ đặt lịch tại BarberPro. Từ yêu cầu sau:
            
            "%s"
            
            Hãy:
            1. Xác định các thông tin: dịch vụ, thời gian, yêu cầu đặc biệt
            2. Nếu thiếu thông tin, hỏi rõ từng mục
            3. Nếu đủ thông tin, tóm tắt lại và đề nghị xác nhận
            
            Ví dụ mẫu:
            - Dịch vụ: Cắt tóc cơ bản
            - Thời gian: 14:00 ngày 20/05
            - Ghi chú: Cắt layer nhẹ
            
            Kết luận: "Vui lòng xác nhận thông tin trên hoặc cung cấp thêm chi tiết."
            """.formatted(userMessage);

        return geminiAIService.generateContent(prompt);
    }

    private String handleGeneralQuestion(String userMessage) {
        String prompt = """
            Bạn là trợ lý ảo của tiệm BarberPro. Hãy trả lời câu hỏi:
            
            "%s"
            
            Yêu cầu:
            1. Ngắn gọn, thân thiện
            2. Nếu liên quan đến tóc, dịch vụ, đặt lịch thì gợi ý chuyển hướng
            3. Giữ phong cách chuyên nghiệp của salon tóc
            
            Trả lời bằng tiếng Việt.
            """.formatted(userMessage);

        return geminiAIService.generateContent(prompt);
    }

    @PostMapping("/confirm-booking")
    public ResponseEntity<Map<String, String>> confirmBooking(@RequestBody Map<String, String> bookingDetails) {
        String prompt = """
            Tạo xác nhận đặt lịch với thông tin:
            
            Tên khách hàng: %s
            Dịch vụ: %s
            Thời gian: %s
            Ghi chú: %s
            
            Yêu cầu:
            1. Cảm ơn khách hàng
            2. Nhắc đến trước 10 phút
            3. Thông báo chính sách hủy lịch (nếu có)
            4. Ký tên: "BarberPro Team"
            """.formatted(
                bookingDetails.getOrDefault("name", "Quý khách"),
                bookingDetails.getOrDefault("service", "chưa chọn"),
                bookingDetails.getOrDefault("time", "chưa chọn"),
                bookingDetails.getOrDefault("note", "không có")
        );

        String confirmation = geminiAIService.generateContent(prompt);

        return ResponseEntity.ok(Map.of(
                "confirmation", confirmation,
                "status", "success"
        ));
    }
}