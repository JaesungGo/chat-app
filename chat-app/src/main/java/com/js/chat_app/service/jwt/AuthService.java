package com.js.chat_app.service.jwt;

import com.js.chat_app.domain.user.User;
import com.js.chat_app.domain.user.userDTO.LoginRequest;
import com.js.chat_app.repository.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    // 로그인
    public void login(LoginRequest request, HttpServletResponse httpServletResponse) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 이메일 입니다."));

        user.setStatus(User.Status.ACTIVE);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtProvider.createToken(user);
        jwtProvider.addCookie(token,httpServletResponse);
    }

    // 현재 유저정보 찾기
    public User getCurUser(HttpServletRequest httpServletRequest){
        String token = jwtProvider.cookieToToken(httpServletRequest);
        String userEmail = jwtProvider.getEmail(token);
        return userRepository.findByEmail(userEmail)
                .orElseThrow(()-> new RuntimeException("토큰의 이메일이 DB의 이메일과 다르거나 없음"));
    }

    // 로그 아웃
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        String token = jwtProvider.cookieToToken(httpServletRequest);
        String email = jwtProvider.getEmail(token);

        User user = userRepository.findByEmail(email)
                        .orElseThrow(()-> new RuntimeException("존재하지 않는 이메일 입니다."));

        user.setStatus(User.Status.INACTIVE);
        user.setLastLogoutAt(LocalDateTime.now());
        userRepository.save(user);

        jwtProvider.removeCookie(httpServletResponse);
    }
}
