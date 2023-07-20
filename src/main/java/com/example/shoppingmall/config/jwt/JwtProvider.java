package com.example.shoppingmall.config.jwt;

import com.example.shoppingmall.dto.jwt.TokenDTO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {

    private static final String AUTHORITIES_KEY = "auth";

    @Value("${jwt.access.expiration}")
    private long accessTokenTime;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenTime;

    private Key key;

    public JwtProvider(@Value("${jwt.secret_key}") String secret_key) {
        byte[] secretKey = DatatypeConverter.parseBase64Binary(secret_key);
        this.key = Keys.hmacShaKeyFor(secretKey);
    }

    // JWT 생성
    public TokenDTO createToken(Authentication authentication) {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date now2 = new Date();

        // access token 생성
        Date accessTokenExpire = new Date(now + this.accessTokenTime);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(now2)
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpire)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("accessToken : " + accessToken);

        // refresh token 생성
        Date refreshTokenExpire = new Date(now + this.refreshTokenTime);
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(now2)
                .setExpiration(refreshTokenExpire)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("refreshToken : " + refreshToken);

        return TokenDTO.builder()
                .grantType("Bearer ")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenTime(accessTokenExpire)
                .refreshTokenTime(refreshTokenExpire)
                .userEmail(authentication.getName())
                .build();
    }

    // access token 만료시 refresh token으로 access token 발급
    public TokenDTO createAccessToken(String userEmail, List<GrantedAuthority> authorities) {
        Long now = (new Date()).getTime();
        Date now2 = new Date();
        Date accessTokenExpire = new Date(now + this.accessTokenTime);

        String accessToken = Jwts.builder()
                .setIssuedAt(now2)
                .setSubject(userEmail)
                .setExpiration(accessTokenExpire)
                .claim(AUTHORITIES_KEY, authorities )
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("accessToken : " + accessToken);

        return TokenDTO.builder()
                .grantType("Bearer ")
                .accessToken(accessToken)
                .userEmail(userEmail)
                .accessTokenTime(accessTokenExpire)
                .build();
    }

    public Authentication getAuthentication(String token) {
        // 토큰 복호화 메소드
        Claims claims = parseClaims(token);

        if(claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }


        // 클레임 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("ExpiredJwtException : " + e.getMessage());
            log.info("ExpiredJwtException : " + e.getClaims());

            return e.getClaims();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
