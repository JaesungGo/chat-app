package com.js.chat_app.repository.chat;

import com.js.chat_app.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    /**
     * 마지막 메시지의 시간 기준으로 내림차순 정렬된 모든 채팅방을 반환
     * ex ) 카카오톡 채팅방 목록
     */
    List<ChatRoom> findAllByOrderByLastMessageAtDesc();

}
