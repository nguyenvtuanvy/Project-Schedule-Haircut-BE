package com.example.projectschedulehaircutserver.controller.chatbot;

import com.example.projectschedulehaircutserver.exeption.LoginException;
import com.example.projectschedulehaircutserver.request.MessageRequest;
import com.example.projectschedulehaircutserver.service.chatbot.ChatBotService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/web/chatbot")
@AllArgsConstructor
public class ChatBotController {
    private final ChatBotService chatBotService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageRequest request) throws LoginException {
        String response = chatBotService.processMessage(request.getMessage());
        return ResponseEntity.ok(response);
    }

}
