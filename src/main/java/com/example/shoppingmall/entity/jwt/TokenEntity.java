package com.example.shoppingmall.entity.jwt;

import com.example.shoppingmall.dto.jwt.TokenDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "token")
@Getter
@NoArgsConstructor
@ToString
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;
    @Column(name = "grant_type")
    private String grantType;
    @Column(name = "access_token")
    private String accessToken;
    @Column(name = "refresh_token")
    private String refreshToken;
    @Column(name = "member_email")
    private String userEmail;
    @Column(name = "access_token_time")
    private Date accessTokenTime;
    @Column(name = "refresh_token_time")
    private Date refreshTokenTime;

    @Builder
    public TokenEntity(Long id,
                       String grantType,
                       String accessToken,
                       String refreshToken,
                       String userEmail,
                       Date accessTokenTime,
                       Date refreshTokenTime
                       ) {
        this.id = id;
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userEmail = userEmail;
        this.accessTokenTime = accessTokenTime;
        this.refreshTokenTime = refreshTokenTime;
    }

    public static TokenEntity toTokenEntity(TokenDTO tokenDTO) {
        return TokenEntity.builder()
                .id(tokenDTO.getId())
                .grantType(tokenDTO.getGrantType())
                .accessToken(tokenDTO.getAccessToken())
                .refreshToken(tokenDTO.getRefreshToken())
                .userEmail(tokenDTO.getUserEmail())
                .accessTokenTime(tokenDTO.getAccessTokenTime())
                .refreshTokenTime(tokenDTO.getRefreshTokenTime())
                .build();
    }
}
