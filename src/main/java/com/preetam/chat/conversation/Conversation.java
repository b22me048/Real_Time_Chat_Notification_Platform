package com.preetam.chat.conversation;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Getter @Setter @NoArgsConstructor
public class Conversation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ElementCollection
    @CollectionTable(name = "conversation_members", joinColumns = @JoinColumn(name = "conversation_id"))
    @Column(name = "username")
    private Set<String> members = new HashSet<>();

    public Conversation(String name, Set<String> members) {
        this.name = name; this.members = members;
    }
}
