package com.js.chat_app.domain.chat;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_users")
@Data
public class ChatRoomUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatuserId;

    @ManyToOne @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate(){
        joinedAt = LocalDateTime.now();
    }
}
