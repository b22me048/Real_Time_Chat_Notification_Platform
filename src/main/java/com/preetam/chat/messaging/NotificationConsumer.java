package com.preetam.chat.messaging;

import org.slf4j.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {
    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(topics = "chat-notifications", groupId = "chat-notifications")
    public void consume(ChatEvent event) {
        log.info("Notification event: messageId={}, conversationId={}, sender={}",
                event.messageId(), event.conversationId(), event.sender());
    }
}
