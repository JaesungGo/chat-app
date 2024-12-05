package com.js.chat_app.repository.chat;

import com.js.chat_app.domain.chat.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    // 특정 채팅방에 대한 메시지를 생성 시간 기준으로 내림차순 정렬하여 반환
    List<ChatMessage> findByRoomIdOrderByCreatedAtDesc(Long roomId, Pageable pageable);

    // 특정 채팅방 ID와 주어진 시간 이후에 생성된 메시지 반환
    List<ChatMessage> findByRoomIdAndCreatedAtGreaterThan(Long roomId, LocalDateTime dateTime);

}
