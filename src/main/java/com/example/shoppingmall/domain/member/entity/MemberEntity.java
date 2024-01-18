package com.example.shoppingmall.domain.member.entity;

import lombok.*;

import javax.persistence.*;

@Entity(name = "member")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "member_name", nullable = false)
    private String memberName;

    @Column(name = "member_email", nullable = false)
    private String email;

    @Column(name = "member_pw")
    private String memberPw;

    @Column(name = "nick_name", nullable = false)
    private String nickName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    // USER, ADMIN
    private Role memberRole;
    @Embedded
    private AddressEntity address;

    @Builder
    public MemberEntity(Long memberId,
                        Long memberId1,
                        String memberName,
                        String email,
                        String memberPw,
                        String nickName,
                        Role memberRole,
                        AddressEntity address) {
        this.memberId = memberId;
        this.memberId = memberId1;
        this.memberName = memberName;
        this.email = email;
        this.memberPw = memberPw;
        this.nickName = nickName;
        this.memberRole = memberRole;
        this.address = address;
    }

    public void updateMember(ModifyMemberDTO updateMember, String encodePw) {
        MemberEntity.builder()
                .memberId(this.memberId)
                .email(this.email)
                .memberPw(updateMember.getMemberPw() == null
                        ? this.memberPw
                        : encodePw)
                .nickName(updateMember.getNickName() == null
                        ? this.getNickName() : updateMember.getNickName())
                .memberRole(this.memberRole)
                .memberPoint(this.memberPoint)
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
