package com.js.chat_app.domain.chat.chatDTO;

import com.js.chat_app.domain.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {
    private String messageId;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String content;
    private ChatMessage.MessageType type;
    private LocalDateTime createdAt;
    private ChatMessage.MessageStatus status;

    public ChatMessageResponse(ChatMessage message) {
        this.messageId = message.getMessageId();
        this.roomId = message.getRoomId();
        this.senderId = message.getSenderId();
        this.senderName = message.getSenderName();
        this.content = message.getContent();
        this.type = message.getType();
        this.createdAt = message.getCreatedAt();
        this.status = message.getStatus();
    }
}
