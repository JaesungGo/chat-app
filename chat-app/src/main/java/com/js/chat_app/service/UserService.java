package com.js.chat_app.service;


import com.js.chat_app.domain.User;
import com.js.chat_app.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입
    public User createUser(User user){
        // 이메일 중복 체크
        if(userRepository.existsByEmail(user.getEmail())){
            throw new RuntimeException("이메일이 이미 존재합니다.");
        }

        // 비밀번호 암호화
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);

    }

    // 회원 찾기
    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
    }
}
