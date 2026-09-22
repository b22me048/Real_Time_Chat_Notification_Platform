package com.preetam.chat.messaging;

import java.time.Instant;

public record ChatEvent(Long messageId, Long conversationId, String sender, String content, Instant createdAt) {}
