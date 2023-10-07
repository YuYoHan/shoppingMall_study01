package com.example.shoppingmall.dto.jwt;


import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "토큰 타입")
    private String grantType;
    @Schema(description = "access token")
    private String accessToken;
    @Schema(description = "refresh token")
    private String refreshToken;
    @Schema(description = "회원 이메일")
    private String userEmail;
    @Schema(description = "access token 발급 시간")
    private Date accessTokenTime;
    @Schema(description = "refresh token 발급 시간")
    private Date refreshTokenTime;

    @Builder
    public TokenDTO(Long id,
                    String grantType,
                    String accessToken,
                    String refreshToken,
                    String userEmail,
                    Date accessTokenTime,
                    Date refreshTokenTime) {
        this.id = id;
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userEmail = userEmail;
        this.accessTokenTime = accessTokenTime;
        this.refreshTokenTime = refreshTokenTime;
    }

}
