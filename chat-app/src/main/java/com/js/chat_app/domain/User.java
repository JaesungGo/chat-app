package com.js.chat_app.domain;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name ="users")
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userid;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    private String profileImage;

    @Enumerated(EnumType.STRING)
    private String status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    public void onCreate(){
        this.createdAt = LocalDateTime.now();
    }

}
