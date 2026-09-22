package com.preetam.chat.conversation;

import org.springframework.web.bind.annotation.*;
import java.util.Set;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationRepository repo;

    public ConversationController(ConversationRepository repo) { this.repo = repo; }

    record CreateConversation(String name, Set<String> members) {}

    @PostMapping
    public Conversation create(@RequestBody CreateConversation req) {
        return repo.save(new Conversation(req.name(), req.members()));
    }

    @GetMapping("/{id}")
    public Conversation get(@PathVariable Long id) {
        return repo.findById(id).orElseThrow();
    }
}
