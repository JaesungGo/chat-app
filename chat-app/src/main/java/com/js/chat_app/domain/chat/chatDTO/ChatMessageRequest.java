package com.js.chat_app.domain.chat.chatDTO;

import com.js.chat_app.domain.chat.ChatMessage;
import lombok.Data;

@Data
public class ChatMessageRequest {
    private Long roomId;
    private String content;
    private ChatMessage.MessageType type;
}
