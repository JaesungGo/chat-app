package com.js.chat_app.controller.chat;

import com.js.chat_app.domain.chat.ChatMessage;
import com.js.chat_app.domain.chat.chatDTO.ChatMessageRequest;
import com.js.chat_app.domain.chat.chatDTO.ChatMessageResponse;
import com.js.chat_app.domain.user.User;
import com.js.chat_app.service.chat.ChatMessageService;
import com.js.chat_app.service.chat.ChatRoomService;
import com.js.chat_app.service.jwt.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final AuthService authService;
    private final ChatRoomService chatRoomService;
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
     * 채팅방 입장 처리
     * 1. HTTP로 채팅방 참가 처리 (DB 저장)
     * 2. WebSocket으로 입장 메시지 전송
     */
    @PostMapping("/api/chat/rooms/{roomId}/join")
    public ResponseEntity<Void> joinRoom(
            @PathVariable Long roomId,
            @Header("Authorization") String token
    ) {
        User user = authService.getUserFromToken(token);
        chatRoomService.addUser(roomId, user);
        return ResponseEntity.ok().build();
    }

    /**
     * 채팅방 입장시 호출 -> 메시지 전송
     * "/app/chat.enter"로 전송하는 메시지에 대하여 /topic/room.{roomId}의 사용자들에게 브로드 캐스트
     * @param payload
     * @param token
     * @return
     */
    @MessageMapping("/chat.enter")
    @SendTo("/topic/room.{roomId}")
    public void enter(@Payload Map<String,Long> payload, @Header("Authorization") String token){

        Long roomId = payload.get("roomId");
        User enterUser = authService.getUserFromToken(token);

        chatMessageService.enterRoom(roomId, enterUser.getUserId());

        ChatMessageRequest chatMessageRequest = new ChatMessageRequest();
        chatMessageRequest.setRoomId(roomId);
        chatMessageRequest.setType(ChatMessage.MessageType.SYSTEM);
        chatMessageRequest.setContent(enterUser.getUserName()+ "님이 입장하셨습니다.");

        sendMessage(chatMessageRequest,token);

    }

    @MessageMapping("/chat.leave")
    @SendTo("/topic/room.{roomId}")
    public void leave(@Payload Map<String,Long> payload, @Header("Authorization") String token){
        Long roomId = payload.get("roomId");
        User leaveUser = authService.getUserFromToken(token);

        chatRoomService.leaveRoom(roomId,leaveUser);

        ChatMessageRequest chatMessageRequest = new ChatMessageRequest();
        chatMessageRequest.setRoomId(roomId);
        chatMessageRequest.setType(ChatMessage.MessageType.SYSTEM);
        chatMessageRequest.setContent(leaveUser.getUserName()+ "님이 퇴장하셨습니다.");

        sendMessage(chatMessageRequest,token);
    }
}
