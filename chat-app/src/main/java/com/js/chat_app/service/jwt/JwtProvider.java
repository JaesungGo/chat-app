package com.js.chat_app.service.jwt;

import com.js.chat_app.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    // 토큰 ttl은 1시간으로 설정
    private final long tokenTTL = 3600000;

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    /**
     * 토큰 생성 ( 현재 시간 + 만료 시간 + 서명한 비밀 키 ) -> 문자열
     * @param user
     * @return JWT 토큰을 문자열 형태로 반환
     */
    public String createToken(User user){

        Date curDate = new Date();
        Date validateDate = new Date(curDate.getTime() + tokenTTL);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("id",user.getUserId())
                .claim("email",user.getEmail())
                .claim("username",user.getUserName())
                .setIssuedAt(curDate)
                .setExpiration(validateDate)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰 검증 로직
     * @param token
     * @return
     */
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch(Exception e){
            return false;
        }
    }

    /**
     * 토큰에서 정보 추출
     * @return
     */
    public Claims getClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 이메일 과 유저 아이디를 추출하는 메서드들
     * @return
     */
    public String getEmail(String token){
        return getClaims(token).getSubject();
    }

    public Long getUserId(String token){
        return getClaims(token).get("id",Long.class);
    }

    /**
     * secretKey -> 바이트 배열 -> hmac-sha
     * 토큰 암호화/복호화에 사용할 키 생성
     * @return
     */
    private Key getSignKey(){
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }




}
