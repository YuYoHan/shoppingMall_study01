package com.example.shoppingmall.config.jwt;

import com.example.shoppingmall.dto.jwt.TokenDTO;
import com.example.shoppingmall.dto.member.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.*;
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
    public TokenDTO createToken(Authentication authentication, List<GrantedAuthority> authorities) {
        //  UsernamePasswordAuthenticationToken
        //  [Principal=zxzz45@naver.com, Credentials=[PROTECTED], Authenticated=false, Details=null, Granted Authorities=[]]
        // 여기서 Authenticated=false는 아직 정상임
        // 이 시점에서는 아직 실제로 인증이 이루어지지 않았기 때문에 Authenticated 속성은 false로 설정
        // 인증 과정은 AuthenticationManager와 AuthenticationProvider에서 이루어지며,
        // 인증이 성공하면 Authentication 객체의 isAuthenticated() 속성이 true로 변경됩니다.
        log.info("authentication in JwtProvider : " + authentication);

        // userType in JwtProvider : ROLE_USER
        log.info("userType in JwtProvider : " + authorities);

        // 권한 가져오기
        //  authentication 객체에서 권한 정보(GrantedAuthority)를 가져와 문자열 형태로 변환한 후,
        //  쉼표로 구분하여 조인한 결과를 authorities 변수에 저장합니다. 따라서 authorities는 권한 정보를 문자열 형태로 가지게 됩니다.
        // 권한 정보를 문자열로 변환하여 클레임에 추가하는 방식
//        String authorities = authentication.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.joining(","));

        Map<String, Object> claims = new HashMap<>();
        claims.put(AUTHORITIES_KEY, authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        log.info("claims : " + claims);


        long now = (new Date()).getTime();
        Date now2 = new Date();

        // AccessToken 생성
        Date accessTokenExpire = new Date(now + this.accessTokenTime);
        String accessToken = Jwts.builder()
                // 내용 sub : 유저의 이메일
                // 토큰 제목
                // JWT의 "sub" 클레임을 설정하는 메서드입니다.
                // "sub" 클레임은 일반적으로 사용자를 식별하는 용도로 사용되며,
                // 이메일과 같은 사용자의 고유한 식별자를 담고 있을 수 있습니다.
                .setSubject(authentication.getName())
                .setIssuedAt(now2)
                // 클레임 id : 유저 ID
//                .claim(AUTHORITIES_KEY, authorities)
                .setClaims(claims)
                // 내용 exp : 토큰 만료 시간, 시간은 NumericDate 형식(예: 1480849143370)으로 하며
                // 항상 현재 시간 이후로 설정합니다.
                .setExpiration(accessTokenExpire)
                // 서명 : 비밀값과 함께 해시값을 ES256 방식으로 암호화
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();


//         Claims claim = Jwts.parserBuilder()
//                .setSigningKey(key)
//                        .build()
//                                .parseClaimsJws(accessToken).getBody();


        // accessToken in JwtProvider : eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ6eHp6NDVAbmF2ZXIuY2
        // 9tIiwiaWF0IjoxNjg5OTk1MzM3LCJhdXRoIjoiUk9MRV9VU0VSIiwiZXhwIjoxNjkzNTk1MzM3fQ.2_2PR-A
        // X9N0jKDyA7LpK7xRRBZBYZ17_f8Jq2TY4ny8
        log.info("accessToken in JwtProvider : " + accessToken);

        // claim에서 auth 확인 in JwtProvider : ROLE_USER
        log.info("claim에서 accessToken에 담김 auth 확인 in JwtProvider : " + claims);

        // RefreshToken 생성
        Date refreshTokenExpire = new Date(now + this.refreshTokenTime);
        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setClaims(claims)
                .setIssuedAt(now2)
                .setExpiration(refreshTokenExpire)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();



        log.info("refreshToken in JwtProvider : " + refreshToken);
        log.info("claim에서 refreshToken에 담긴 auth 확인 in JwtProvider : " + claims);

        TokenDTO tokenDTO= TokenDTO.builder()
                .grantType("Bearer ")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accessTokenTime(accessTokenExpire)
                .refreshTokenTime(refreshTokenExpire)
                // principalDeatails에서 getUserName 메소드가 반환한 것을 담아준다.
                // 이메일을 반환하도록 구성했으니 이메일이 반환됩니다.
                .userEmail(authentication.getName())
                .build();

        log.info("tokenDTO in JwtProvider : " + tokenDTO);

        return tokenDTO;
    }

    // access token 만료시 refresh token으로 access token 발급
    public TokenDTO createAccessToken(String userEmail, List<GrantedAuthority> authorities) {
        Long now = (new Date()).getTime();
        Date now2 = new Date();
        Date accessTokenExpire = new Date(now + this.accessTokenTime);

        log.info("authorities : " + authorities);

        Map<String, Object> claims = new HashMap<>();
        claims.put(AUTHORITIES_KEY, authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        log.info("claims : " + claims);

        String accessToken = Jwts.builder()
                .setIssuedAt(now2)
                .setSubject(userEmail)
                .setExpiration(accessTokenExpire)
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        log.info("accessToken in JwtProvider : " + accessToken);

        TokenDTO tokenDTO = TokenDTO.builder()
                .grantType("Bearer ")
                .accessToken(accessToken)
                .userEmail(userEmail)
                .accessTokenTime(accessTokenExpire)
                .build();

        log.info("tokenDTO in JwtProvider : " + tokenDTO);
        return tokenDTO;
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 코드
    // 토큰으로 클레임을 만들고 이를 이용해 유저 객체를 만들어서 최종적으로 authentication 객체를 리턴
    // 인증 정보 조회
    public Authentication getAuthentication(String token) {
        // 토큰 복호화 메소드
        Claims claims = parseClaims(token);
        log.info("claims in JwtProvider  : " + claims);

        if(claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        Object auth = claims.get("auth");
        // [ROLE_USER]
        log.info("auth in JwtProvider : " + auth);

        // 클레임 권한 정보 가져오기
        List<String> authorityStrings = (List<String>) claims.get(AUTHORITIES_KEY);
        // [ROLE_USER]
        log.info("authorityStrings in JwtProvider : " + authorityStrings);

        Collection<? extends GrantedAuthority> authorities =
                authorityStrings.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // [ROLE_USER]
        log.info("authorities in JwtProvider : " + authorities);

        // UserDetails 객체를 만들어서 Authentication 리턴
//        User principal = new User(claims.getSubject(), "", authorities);
//        log.info("principal in JwtProvider : " + principal);

        return new UsernamePasswordAuthenticationToken(claims.getSubject(), token, authorities);
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
