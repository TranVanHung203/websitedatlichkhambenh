package tlcn.quanlyphongkham.controllers;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import tlcn.quanlyphongkham.entities.TinNhan;
import tlcn.quanlyphongkham.repositories.TinNhanRepository;

@Controller
public class ChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private TinNhanRepository tinNhanRepository;

    @MessageMapping("/chat")
    public void sendMessage(@Payload TinNhan tinNhan) {
        tinNhan.setId(UUID.randomUUID().toString());
        tinNhan.setThoiGian(LocalDateTime.now());
        tinNhanRepository.save(tinNhan);
        messagingTemplate.convertAndSend("/topic/messages", tinNhan);
    }
}