package com.js.chat_app.controller.chat;

import com.js.chat_app.domain.chat.ChatMessage;
import com.js.chat_app.domain.chat.chatDTO.ChatMessageRequest;
import com.js.chat_app.domain.chat.chatDTO.ChatMessageResponse;
import com.js.chat_app.domain.user.User;
import com.js.chat_app.service.chat.ChatMessageService;
import com.js.chat_app.service.jwt.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final AuthService authService;
    private final ChatMessageService chatMessageService;

    /**
     * 메시지 전송시 호출
     * "/app/chat.send"로 전송하는 메시지에 대하여 "/topic/room.{roomId}의 사용자들에게 전송 (브로드 캐스트)
     * @param request
     * @param token
     * @return
     */
    @MessageMapping("/chat.send")
    @SendTo("/topic/room.{roomId}")
    public ChatMessageResponse sendMessage(@Payload ChatMessageRequest request, @Header("Authorization") String token){
        User sender = authService.getUserFromToken(token);

        ChatMessage message = chatMessageService.saveAndSendMessage(request,sender);

        return new ChatMessageResponse(message);
    }

    /**
     * 채팅방 입장시 호출 -> 메시지 전송
     * "/app/chat.enter"로 전송하는 메시지에 대하여 /topic/room.{roomId}의 사용자들에게 브로드 캐스트
     * @param request
     * @param token
     * @return
     */
    @MessageMapping("/chat.enter")
    @SendTo("/topic/room.{roomId}")
    public ChatMessageResponse enter(@Payload ChatMessageRequest request, @Header("Authorization") String token){

        User enterUser = authService.getUserFromToken(token);

        request.setType(ChatMessage.MessageType.SYSTEM);
        request.setContent(enterUser.getUserName()+ "님이 입장하셨습니다.");

        ChatMessage message = chatMessageService.saveAndSendMessage(request,enterUser);
        return new ChatMessageResponse(message);
    }
}
