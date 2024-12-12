package com.js.chat_app.domain.chat;

import com.js.chat_app.domain.user.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_users")
@Data
public class ChatRoomUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatUserId;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    private RoomRole roomRole;

    @PrePersist
    protected void onCreate(){
        joinedAt = LocalDateTime.now();
    }

    public enum RoomRole{
        ROOM_MANAGER,
        ROOM_MEMBER
    }
}
