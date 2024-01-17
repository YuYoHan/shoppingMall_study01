package com.example.shoppingmall.domain.jwt.dto;

import com.example.shoppingmall.domain.jwt.entity.TokenEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class TokenDTO {
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String memberEmail;
    private Long memberId;

    @Builder
    public TokenDTO(String grantType,
                    String accessToken,
                    String refreshToken,
                    String memberEmail,
                    Long memberId) {
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.memberEmail = memberEmail;
        this.memberId = memberId;
    }

    public static TokenDTO changeDTO(TokenEntity tokenEntity) {
        return TokenDTO.builder()
                .grantType(tokenEntity.getGrantType())
                .accessToken(tokenEntity.getAccessToken())
                .refreshToken(tokenEntity.getRefreshToken())
                .memberEmail(tokenEntity.getMemberEmail())
                .memberId(tokenEntity.getMemberId())
                .build();
    }

}
