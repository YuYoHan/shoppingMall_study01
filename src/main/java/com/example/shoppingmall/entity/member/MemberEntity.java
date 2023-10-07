package com.example.shoppingmall.entity.member;

import com.example.shoppingmall.dto.member.Role;
import com.example.shoppingmall.entity.base.BaseEntity;
import com.example.shoppingmall.entity.member.embedded.AddressEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "member")
@Getter
@ToString
@NoArgsConstructor
public class MemberEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long userId;

    @Column(name = "member_name", nullable = false)
    private String userName;

    @Column(name = "member_email", nullable = false)
    private String userEmail;

    @Column(name = "member_pw")
    private String userPw;

    @Column(name = "member_nickName", unique = true)
    private String nickName;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false)
    // ROLE_USER, ROLE_ADMIN
    private Role role;

    // OAuth2 가입할 때를 위해서
    @Column(name = "provider")
    private String provider;
    @Column(name = "provider_id")
    private String providerId;

    @Embedded
    @Column(name = "member_addr")
    private AddressEntity address;

    @Builder
    public MemberEntity(
            Long userId,
            String userName,
            String userEmail,
            String userPw,
            String nickName,
            Role role,
            String provider,
            String providerId,
            AddressEntity address) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPw = userPw;
        this.nickName = nickName;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
        this.address = address;
    }


}
