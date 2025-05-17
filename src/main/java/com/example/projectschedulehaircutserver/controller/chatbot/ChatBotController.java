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
        Bạn là chuyên gia tóc BarberPro. Hãy gợi ý kiểu tóc với định dạng:
        
        **Gợi ý kiểu tóc phù hợp**
        
        ✂️ [Tên kiểu 1]
        - Đặc điểm: [Mô tả]
        - Ưu điểm: [Lợi ích]
        - Phù hợp: [Đối tượng]
        
        ✂️ [Tên kiểu 2]
        - Đặc điểm: [Mô tả]
        - Ưu điểm: [Lợi ích]
        - Phù hợp: [Đối tượng]
        
        Yêu cầu:
        1. Mỗi mục gạch đầu dòng phải xuống dòng mới
        2. Dùng ký tự xuống dòng \\n thực sự
        3. Giới hạn 2-3 kiểu tóc
        4. Mỗi kiểu tóc không quá 3 dòng mô tả
        """.formatted(userMessage);

        return geminiAIService.generateContent(prompt);
    }

    private String handleServiceInquiry() {
        return """
            DANH SÁCH DỊCH VỤ NGẮN GỌN:
            1. Cắt tóc: 150k
            2. Cạo mặt: 100k
            3. Nhuộm tóc: 300k
            """;
    }

    private String handleGeneralQuestion(String userMessage) {
        String prompt = """
            Bạn là trợ lý BarberPro. Trả lời ngắn gọn (dưới 50 từ):
            
            "%s"
            """.formatted(userMessage);

        return geminiAIService.generateContent(prompt);
    }
}