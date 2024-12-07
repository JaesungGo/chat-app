package com.js.chat_app.domain.user;

import lombok.Builder;
import lombok.Data;

public class UserDTO {

    @Data @Builder
    public class SignupRequest{
        private String email;
        private String password;
        private String username;
    }

    @Data @Builder
    public class LoginRequest{
        public String email;
        public String password;
    }

    @Data @Builder
    public class TokenResponse{
        public String accessToken;
        public String email;
        public String username;

    }
}
