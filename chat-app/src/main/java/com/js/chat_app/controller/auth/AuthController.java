package com.js.chat_app.controller.auth;


import com.js.chat_app.domain.user.User;
import com.js.chat_app.domain.user.userDTO.LoginRequest;
import com.js.chat_app.domain.user.userDTO.SignUpRequest;
import com.js.chat_app.service.jwt.AuthService;
import com.js.chat_app.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignUpRequest request){
        userService.createUser(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody LoginRequest request, HttpServletResponse httpServletResponse){
        authService.login(request,httpServletResponse);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
        authService.logout(httpServletRequest, httpServletResponse);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/userinfo")
    public ResponseEntity<User> getUserEmail(HttpServletRequest httpServletRequest){
        User curUser = authService.getCurUser(httpServletRequest);
        return ResponseEntity.ok(curUser);
    }

}
