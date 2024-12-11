package com.js.chat_app.service.chat;

import com.js.chat_app.domain.chat.ChatMessage;
import com.js.chat_app.domain.chat.chatDTO.ChatMessageRequest;
import com.js.chat_app.domain.chat.chatDTO.ChatMessageResponse;
import com.js.chat_app.domain.user.User;
import com.js.chat_app.repository.chat.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j @RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 메시지를 저장하고 WebSocket을 통해 구독자들에게 보낸다. 최근 메시지를 redis에서 캐싱을 한다.
     * @param request
     * @param sender
     * @return
     */
    public ChatMessage saveAndSendMessage(ChatMessageRequest request, User sender){

        ChatMessage message = ChatMessage.builder()
                .roomId(request.getRoomId())
                .senderId(sender.getUserId())
                .senderName(sender.getUserName())
                .content(request.getContent())
                .type(request.getType())
                .status(ChatMessage.MessageStatus.SENT)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        String latestMessageKey = "chat:room" + request.getRoomId() + ":latest";
        redisTemplate.opsForValue().set(latestMessageKey, message);

        messagingTemplate.convertAndSend("/topic/room." + request.getRoomId(), new ChatMessageResponse(savedMessage));

        return savedMessage;
    }

    /**
     * 읽은 메시지 수신 표시
     * @param messageId
     * @param userId
     */
    public void readMessageCheck(String messageId, Long userId){
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(()-> new RuntimeException("메시지를 찾을 수 없습니다."));

        // readUser list에 추가
        if (!message.getReadUserId().contains(userId)) {
            message.getReadUserId().add(userId);
            message.setStatus(ChatMessage.MessageStatus.READ);
            chatMessageRepository.save(message);
        }

        messagingTemplate.convertAndSend(
                "/topic/room." + message.getRoomId() + ".read",
                Map.of("messageId",messageId,"userId",userId)
        );

    }


    /**
     * 채팅방의 이전 메시지를 조회하기 위한 페이징 처리 (스크롤 시)
     * @param roomId
     * @param page
     * @param size
     * @return
     */
    public List<ChatMessage> getRoomMessageHistory(Long roomId, int page, int size){
        return chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, PageRequest.of(page, size));
    }

}
