package com.js.chat_app.service.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");
        String token = null;

        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            token = bearerToken.substring(7);
        }

        try{
            if(token != null && jwtProvider.validateToken(token)){
                String userEmail = jwtProvider.getEmail(token);

                UserDetails userDetails = User.builder()
                        .username(userEmail)
                        .password("")
                        .authorities(String.valueOf(com.js.chat_app.domain.User.Role.MEMBER))
                        .build();

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        catch (Exception e){
            log.error("유저 인증 정보를 넣을 수 없습니다.",e);
        }

        filterChain.doFilter(request,response);

    }
}
