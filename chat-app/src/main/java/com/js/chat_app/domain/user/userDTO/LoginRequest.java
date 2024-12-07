package com.js.chat_app.domain.user.userDTO;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class LoginRequest {
    private String email;
    private String password;
}