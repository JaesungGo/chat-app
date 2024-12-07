package com.js.chat_app.domain.chat;

import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "messages")
@Data @Builder
public class ChatMessage {

    @Id
    private String messageId;

    @Indexed
    private Long roomId;

    @Indexed
    private Long senderId;

    private String senderName;

    private MessageType type;

    private String content;

    @Indexed
    private LocalDateTime createdAt;

    private MessageStatus status;

    private LocalDateTime readAt;
    private List<Long> readUserId;
    public enum MessageType{
        TEXT,
        IMAGE,
        FILE,
        SYSTEM
    }
    public enum MessageStatus{
        SENT,
        DELIVERED,
        READ
    }

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
        readUserId = new ArrayList<>();
    }
}
