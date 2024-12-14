package com.js.chat_app.service.chat;

import com.js.chat_app.domain.user.User;
import com.js.chat_app.domain.chat.ChatRoom;
import com.js.chat_app.domain.chat.ChatRoomUser;
import com.js.chat_app.repository.chat.ChatMessageRepository;
import com.js.chat_app.repository.chat.ChatRoomRepository;
import com.js.chat_app.repository.chat.ChatRoomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageCountService messageCountService;

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

    /**
     * 모든 방 목록 가져오기
     * @return
     */
    public List<ChatRoom> getAllRooms(){
        return chatRoomRepository.findAll();
    }

    /**
     * 유저가 방에 있는 지 확인
     */
    public boolean isUserValidate(Long roomId, Long userId){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new RuntimeException("채팅방이 없습니다."));

        return chatRoomUserRepository.findByChatRoomAndUser(chatRoom, User.builder().userId(userId).build()).isPresent();

    }

    /**
     * 채팅방 나가기
     * - 채팅방 마지막 사용자일 경우 채팅방 삭제
     * - 그렇지 않은 경우 사용자만 제거
     * @param roomId
     * @param user
     */
    @Transactional
    public void leaveRoom(Long roomId, User user){

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(()-> new RuntimeException("채팅방을 찾을 수 없습니다."));

        ChatRoomUser chatRoomUser = chatRoomUserRepository.findByChatRoomAndUser(chatRoom,user)
                .orElseThrow(()-> new RuntimeException("해당 채팅방에서 사용자를 찾을 수 없습니다."));

        Long userCount = chatRoomUserRepository.countByChatRoom(chatRoom);

        if(userCount <= 1) {
            chatRoomRepository.delete(chatRoom);
            chatRoomUserRepository.delete(chatRoomUser);
            chatMessageRepository.deleteByRoomId(roomId);

        }else{
            chatRoomUserRepository.delete(chatRoomUser);
            messageCountService.resetCount(roomId,user.getUserId());
        }

        if(chatRoomUser.getRoomRole() == ChatRoomUser.RoomRole.ROOM_MANAGER){
            ChatRoomUser nextManager = chatRoomUserRepository.findFirstByChatRoomAndRoomRoleNot(chatRoom, ChatRoomUser.RoomRole.ROOM_MANAGER).orElseThrow(()->new RuntimeException("방장을 넘길 수 없습니다."));

            nextManager.setRoomRole(ChatRoomUser.RoomRole.ROOM_MANAGER);
            chatRoomUserRepository.save(nextManager);
        }

    }

}
