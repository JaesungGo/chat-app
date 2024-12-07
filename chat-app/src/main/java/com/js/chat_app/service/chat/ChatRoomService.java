package com.js.chat_app.service.chat;

import com.js.chat_app.domain.user.User;
import com.js.chat_app.domain.chat.ChatRoom;
import com.js.chat_app.domain.chat.ChatRoomUser;
import com.js.chat_app.repository.chat.ChatRoomRepository;
import com.js.chat_app.repository.chat.ChatRoomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    /**
     * 방을 생성하고, 방장을 방에다 추가
     * @param roomName : 방이름
     * @param createUser : 방생성자
     * @return
     */
    public ChatRoom createRoom(String roomName, User createUser){
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setRoomName(roomName);

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        ChatRoomUser chatRoomUser = new ChatRoomUser();
        chatRoomUser.setChatRoom(savedRoom);
        chatRoomUser.setUser(createUser);
        chatRoomUser.setRoomRole(ChatRoomUser.RoomRole.ROOM_MANAGER);
        chatRoomUserRepository.save(chatRoomUser);

        return savedRoom;
    }

    /**
     * 유저를 추가하는 로직
     * @param roomId
     * @param user
     */
    public void addUser(Long roomId, User user){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new RuntimeException("채팅방을 찾을 수 없습니다."));

        if(chatRoomUserRepository.findByChatRoomAndUser(chatRoom,user).isPresent()){
            throw new RuntimeException("유저가 이미 방에 존재합니다.");
        }

        ChatRoomUser chatRoomUser = new ChatRoomUser();
        chatRoomUser.setChatRoom(chatRoom);
        chatRoomUser.setUser(user);
        chatRoomUser.setRoomRole(ChatRoomUser.RoomRole.ROOM_MEMBER);

        chatRoomUserRepository.save(chatRoomUser);
    }

}
