package com.js.chat_app.controller.chat;

import com.js.chat_app.domain.chat.ChatMessage;
import com.js.chat_app.domain.chat.ChatRoom;
import com.js.chat_app.domain.user.User;
import com.js.chat_app.service.chat.ChatMessageService;
import com.js.chat_app.service.chat.ChatRoomService;
import com.js.chat_app.service.jwt.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;
    private final AuthService authService;

    /**
     * 방 생성 호출
     * @param roomName
     * @param token
     * @return
     */
    @PostMapping("/rooms")
    public ResponseEntity<?> createRoom(
            @RequestBody String roomName,
            @Header("Authorization") String token
    ) {
        User user = authService.getUserFromToken(token);
        ChatRoom chatRoom = chatRoomService.createRoom(roomName,user);
        return ResponseEntity.ok(chatRoom);
    }

    /**
     * 방 리스트 가져오기
     * @return
     */
    @GetMapping("/rooms")
    public ResponseEntity<?> getRooms(){
        List<ChatRoom> rooms = chatRoomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    /**
     * 채팅방 참가 호출
     * @param roomId
     * @param token
     * @return
     */
    @PostMapping("/rooms/{roomId}/join")
    public ResponseEntity<?> joinRoom(
            @PathVariable Long roomId,
            @Header("Authorization") String token
    ){
        User user = authService.getUserFromToken(token);
        chatRoomService.addUser(roomId, user);
        return ResponseEntity.ok().build();
    }

    /**
     * 새로 들어간 채팅방의 메시지 이력 호출 (0~20)
     * @param roomId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<?> getRoomHistory(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "20") int size
    ){
        List<ChatMessage> messages = chatMessageService.getRoomMessageHistory(roomId,page,size);
        return ResponseEntity.ok(messages);
    }


    @GetMapping("/rooms/{roomId}/unread")
    public ResponseEntity<Long> getUnreadCount(
            @PathVariable Long roomId,
            @Header("Authorization") String token
    ){
        User user = authService.getUserFromToken(token);
        Long count = chatMessageService.countUnreadMessage(roomId, user.getUserId());
        return ResponseEntity.ok(count);
    }
}
