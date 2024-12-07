package com.js.chat_app.domain.user.userDTO;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class SignUpRequest {
    private String email;
    private String password;
    private String username;
}