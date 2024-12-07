package com.js.chat_app.service.jwt;

import com.js.chat_app.domain.user.User;
import com.js.chat_app.domain.user.UserDTO;
import com.js.chat_app.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public UserDTO.TokenResponse login(UserDTO.LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일 입니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtProvider.createToken(user);

        return UserDTO.TokenResponse.builder()
                .accessToken(token)
                .email(user.getEmail())
                .username(user.getUserName())
                .build();

    }

    public User getCurUser(String token){
        String userEmail = jwtProvider.getEmail(token);
        return userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new RuntimeException("토큰의 이메일이 DB의 이메일과 다르거나 없음"));
    }

}
