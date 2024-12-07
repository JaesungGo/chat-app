package com.js.chat_app.service.user;


import com.js.chat_app.domain.user.User;
import com.js.chat_app.domain.user.UserDTO;
import com.js.chat_app.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입
    public void createUser(UserDTO.SignupRequest request){

        // 이메일 중복 체크
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("이메일이 이미 존재합니다.");
        }

        // 비밀번호 암호화
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userName(request.getUsername())
                .status("ACTIVE")
                .build();

        userRepository.save(user);

    }

    // 회원 찾기
    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
    }

    public void updateLastLogin(String email){
        User findUser = findByEmail(email);
        findUser.setLastLoginAt(LocalDateTime.now());
        userRepository.save(findUser);
    }
}
