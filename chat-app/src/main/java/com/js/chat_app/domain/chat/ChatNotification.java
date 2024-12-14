package com.js.chat_app.domain.chat;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collation = "notifications")
@Data @Builder
public class ChatNotification {

    @Id
    private String id;

    @Indexed
    private Long userId;
    private Long roomId;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;

    public enum NotificationType {
        NEW_MESSAGE,
        ROOM_INVITE
    }
}
