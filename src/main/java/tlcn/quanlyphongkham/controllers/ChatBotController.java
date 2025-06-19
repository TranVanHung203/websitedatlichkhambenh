package tlcn.quanlyphongkham.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import tlcn.quanlyphongkham.services.ChatbotService;

import java.util.Map;

@Controller
@RequestMapping("/chatbot")
public class ChatBotController {

    @Autowired
    private ChatbotService chatbotService;

    @GetMapping
    public String showChatbotView() {
        return "/web/chat/chatbot";
    }

    @PostMapping("/ask")
    @ResponseBody
    public ResponseEntity<String> ask(@RequestBody Map<String, String> request) {
        try {
            String userInput = request.get("userInput");
            if (userInput == null || userInput.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Vui lòng nhập câu hỏi hoặc triệu chứng.");
            }

            String response = chatbotService.handleUserInput(userInput.trim());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Đã xảy ra lỗi: " + e.getMessage());
        }
    }
}
