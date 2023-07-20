package com.example.shoppingmall.dto.jwt;


import com.example.shoppingmall.entity.jwt.TokenEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;

import java.util.Date;

@Getter
@ToString
@NoArgsConstructor
public class TokenDTO {
    @JsonIgnore
    private Long id;
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String userEmail;
    private String nickName;
    private Long userId;
    private Date accessTokenTime;
    private Date refreshTokenTime;

    @Builder
    public TokenDTO(Long id,
                    String grantType,
                    String accessToken,
                    String refreshToken,
                    String userEmail,
                    String nickName,
                    Long userId,
                    Date accessTokenTime,
                    Date refreshTokenTime) {
        this.id = id;
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userEmail = userEmail;
        this.nickName = nickName;
        this.userId = userId;
        this.accessTokenTime = accessTokenTime;
        this.refreshTokenTime = refreshTokenTime;
    }

    public static TokenDTO toTokenDTO(TokenEntity tokenEntity) {
        TokenDTO tokenDTO = TokenDTO.builder()
                .grantType(tokenEntity.getGrantType())
                .accessToken(tokenEntity.getAccessToken())
                .refreshToken(tokenEntity.getRefreshToken())
                .userEmail(tokenEntity.getUserEmail())
                .nickName(tokenEntity.getNickName())
                .userId(tokenEntity.getId())
                .accessTokenTime(tokenEntity.getAccessTokenTime())
                .refreshTokenTime(tokenEntity.getRefreshTokenTime())
                .build();

        return tokenDTO;
    }
}
