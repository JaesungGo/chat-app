package com.js.chat_app.repository.chat;

import com.js.chat_app.domain.user.User;
import com.js.chat_app.domain.chat.ChatRoom;
import com.js.chat_app.domain.chat.ChatRoomUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {

    /**
     * 특정 채팅방에 속한 모든 사용자 목록 반환
     */
    List<ChatRoomUser> findByChatRoom(ChatRoom chatroom);

    /**
     * 특정 채팅방과 사용자에 대한 관계를 검색, 존재하면 Optional로 반환
     */
    Optional<ChatRoomUser> findByChatRoomAndUser(ChatRoom chatroom, User user);

    /**
     * 채팅방 참여 인원을 카운트
     */
    Long countByChatRoom(ChatRoom chatRoom);

    /**
     * 채팅방에서 방장이 아닌 첫 번째 사용자를 찾아 반환
     */
    Optional<ChatRoomUser> findFirstByChatRoomAndRoomRoleNot(ChatRoom chatRoom, ChatRoomUser.RoomRole roomRole);

}
