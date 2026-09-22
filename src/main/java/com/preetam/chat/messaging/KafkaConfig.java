package com.preetam.chat.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.*;

@Configuration
public class KafkaConfig {
    @Bean
    NewTopic chatNotifications() {
        return new NewTopic("chat-notifications", 1, (short) 1);
    }
}
