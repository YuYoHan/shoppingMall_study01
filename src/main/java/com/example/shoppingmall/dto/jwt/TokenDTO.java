package com.example.shoppingmall.dto.jwt;


import com.example.shoppingmall.dto.member.Role;
import com.example.shoppingmall.entity.jwt.TokenEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.usertype.UserType;

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
    private Role role;

    @Builder
    public TokenDTO(Long id,
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
