package com.js.chat_app.service.chat;

import com.js.chat_app.domain.chat.ChatMessage;
import com.js.chat_app.domain.chat.ChatRoom;
import com.js.chat_app.domain.chat.ChatRoomUser;
import com.js.chat_app.domain.chat.chatDTO.ChatMessageRequest;
import com.js.chat_app.domain.user.User;
import com.js.chat_app.repository.chat.ChatMessageRepository;
import com.js.chat_app.repository.chat.ChatRoomUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j @RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final MessageCountService messageCountService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 메시지 저장 및 전송
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

        // 채팅방 참여자 목록 조회
        List<ChatRoomUser> roomUsers = chatRoomUserRepository.findByChatRoom(
                ChatRoom.builder().roomId(request.getRoomId()).build()
        );

        // 참여자 ID 추출
        List<Long> userIds = roomUsers.stream().map(user -> user.getUser().getUserId()).toList();

        messageCountService.incrementUnreadCount(
                request.getRoomId(),
                sender.getUserId(),
                userIds
        );

        return savedMessage;
    }

    /**
     * 채팅방 메시지 조회
     * 채팅방의 이전 메시지를 조회하기 위한 페이징 처리 (스크롤 시)
     * @param roomId
     * @param page
     * @param size
     * @return
     */
    public List<ChatMessage> getRoomMessageHistory(Long roomId, int page, int size){
        return chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId, PageRequest.of(page, size));
    }

    /**
     * 채팅방 입장 처리
     * @param roomId
     * @param userId
     */
    public void enterRoom(Long roomId, Long userId){
        messageCountService.resetCount(roomId,userId);
    }

    public void checkReadMessage(String messageId, Long userId){
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(()-> new RuntimeException("메시지를 찾을 수 없습니다."));

        if(!message.getReadUserId().contains(userId)){
            message.getReadUserId().add(userId);
            updateMessageStatus(message);
            chatMessageRepository.save(message);

            simpMessagingTemplate.convertAndSend(
                    "/topic/room." + message.getRoomId() + ".read",
                    Map.of("messageId",messageId,"userId",userId)
            );
        }
    }

    /**
     * 읽지 않은 메시지 조회
     * @param roomId
     * @param userId
     * @return
     */
    public Long countUnreadMessage(Long roomId, Long userId) {
        return messageCountService.getCount(roomId, userId);
    }

    /**
     * 메시지 읽었을 때 READ로 상태 변화
     * @param message
     */
    private void updateMessageStatus(ChatMessage message){
        Long userSize = chatRoomUserRepository.countByChatRoom(
                ChatRoom.builder().roomId(message.getRoomId()).build()
        );
        if(message.getReadUserId().size() == userSize){
            message.setStatus(ChatMessage.MessageStatus.READ);
        }else if(message.getReadUserId().size() > 1){
            message.setStatus(ChatMessage.MessageStatus.DELIVERED);
        }
    }
}
