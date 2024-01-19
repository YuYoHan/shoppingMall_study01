package com.example.shoppingmall.domain.member.entity;

import com.example.shoppingmall.domain.member.dto.ModifyMemberDTO;
import lombok.*;

import javax.persistence.*;

@Entity(name = "member")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialMemberEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @Column(name = "member_email", nullable = false)
    private String email;

    @Column(name = "nick_name", nullable = false)
    private String nickName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    // USER, ADMIN
    private Role memberRole;
    @Embedded
    private AddressEntity address;

    private String provider;
    private String providerId;

    @Builder
    public SocialMemberEntity(Long memberId,
                              Long memberId1,
                              String memberName,
                              String email,
                              String nickName,
                              Role memberRole,
                              String provider,
                              String providerId,
                              AddressEntity address) {
        this.memberId = memberId;
        this.memberId = memberId1;
        this.memberName = memberName;
        this.email = email;
        this.nickName = nickName;
        this.memberRole = memberRole;
        this.provider = provider;
        this.providerId = providerId;
        this.address = address;
    }

    public void updateMember(ModifyMemberDTO updateMember, String encodePw) {
        SocialMemberEntity.builder()
                .memberId(this.memberId)
                .email(this.email)
                .nickName(updateMember.getNickName() == null
                        ? this.getNickName() : updateMember.getNickName())
                .memberRole(this.memberRole)
                .memberName(this.memberName)
                .address(AddressEntity.builder()
                        .memberAddr(updateMember.getMemberAddress().getMemberAddr() == null
                                ? this.address.getMemberAddr() : updateMember.getMemberAddress().getMemberAddr())
                        .memberAddrDetail(updateMember.getMemberAddress().getMemberAddrDetail() == null
                                ? this.address.getMemberAddrDetail() : updateMember.getMemberAddress().getMemberAddrDetail())
                        .memberZipCode(updateMember.getMemberAddress().getMemberZipCode() == null
                                ? this.address.getMemberZipCode() : updateMember.getMemberAddress().getMemberZipCode())
                        .build()).build();
    }
}
