package com.preetam.chat.message;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findTop100ByConversationIdOrderByCreatedAtDesc(Long conversationId);
}
