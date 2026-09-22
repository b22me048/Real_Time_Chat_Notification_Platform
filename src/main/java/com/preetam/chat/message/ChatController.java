package com.preetam.chat.message;

import com.preetam.chat.messaging.ChatEvent;
import com.preetam.chat.security.JwtService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
public class ChatController {
    private final MessageRepository messages;
    private final JwtService jwt;
    private final KafkaTemplate<String, ChatEvent> kafka;
    private final StringRedisTemplate redis;

    public ChatController(MessageRepository messages, JwtService jwt,
                          KafkaTemplate<String, ChatEvent> kafka,
                          StringRedisTemplate redis) {
        this.messages = messages; this.jwt = jwt; this.kafka = kafka; this.redis = redis;
    }

    record IncomingMessage(String token, String content) {}

    @MessageMapping("/chat/{conversationId}")
    @SendTo("/topic/conversation/{conversationId}")
    public ChatMessage send(@DestinationVariable Long conversationId,
                            IncomingMessage incoming) {
        if (incoming == null || !jwt.valid(incoming.token()))
            throw new IllegalArgumentException("Invalid JWT");
        String username = jwt.extractUsername(incoming.token());
        if (incoming.content() == null || incoming.content().isBlank())
            throw new IllegalArgumentException("Message cannot be empty");

        ChatMessage saved = messages.save(new ChatMessage(conversationId, username, incoming.content()));
        redis.opsForValue().set("presence:" + username, "online");
        kafka.send("chat-notifications",
                new ChatEvent(saved.getId(), conversationId, username,
                        saved.getContent(), saved.getCreatedAt()));
        return saved;
    }

    @GetMapping("/api/conversations/{conversationId}/messages")
    @ResponseBody
    public List<ChatMessage> history(@PathVariable Long conversationId) {
        return messages.findTop100ByConversationIdOrderByCreatedAtDesc(conversationId);
    }
}
