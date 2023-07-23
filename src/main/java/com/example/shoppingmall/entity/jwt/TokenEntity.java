package com.example.shoppingmall.entity.jwt;

import com.example.shoppingmall.dto.member.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity(name = "token")
@Getter
@NoArgsConstructor
@ToString
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String userEmail;
    private String nickName;
    private Long userId;
    private Date accessTokenTime;
    private Date refreshTokenTime;
    private Role role;


    @Builder
    public TokenEntity(Long id,
                       String grantType,
                       String accessToken,
                       String refreshToken,
                       String userEmail,
                       String nickName,
                       Long userId,
                       Date accessTokenTime,
                       Date refreshTokenTime,
                       Role role) {
        this.id = id;
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userEmail = userEmail;
        this.nickName = nickName;
        this.userId = userId;
        this.accessTokenTime = accessTokenTime;
        this.refreshTokenTime = refreshTokenTime;
        this.role = role;
    }
}
