package com.example.shoppingmall.domain.jwt.entity;

import com.example.shoppingmall.domain.jwt.dto.TokenDTO;
import lombok.*;

import javax.persistence.*;

@Entity(name = "token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
public class TokenEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String memberEmail;
    private Long memberId;

    @Builder
    public TokenEntity(Long id,
                       String grantType,
                       String accessToken,
                       String refreshToken,
                       String memberEmail,
                       Long memberId) {
        this.id = id;
        this.grantType = grantType;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.memberEmail = memberEmail;
        this.memberId = memberId;
    }

    // 토큰 변환
    public static TokenEntity changeEntity(TokenDTO tokenDTO) {
        return TokenEntity.builder()
                .grantType(tokenDTO.getGrantType())
                .accessToken(tokenDTO.getAccessToken())
                .refreshToken(tokenDTO.getRefreshToken())
                .memberEmail(tokenDTO.getMemberEmail())
                .memberId(tokenDTO.getMemberId())
                .build();
    }

    // 토큰 업데이트
    public static TokenEntity updateToken(Long id, TokenDTO tokenDTO) {
        return TokenEntity.builder()
                .id(id)
                .grantType(tokenDTO.getGrantType())
                .accessToken(tokenDTO.getAccessToken())
                .refreshToken(tokenDTO.getRefreshToken())
                .memberEmail(tokenDTO.getMemberEmail())
                .memberId(tokenDTO.getMemberId())
                .build();
    }
}
