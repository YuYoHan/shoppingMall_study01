package com.example.shoppingmall.domain.member.entity;

import com.example.shoppingmall.domain.member.dto.ModifyMemberDTO;
import com.example.shoppingmall.domain.member.dto.RequestMemberDTO;
import lombok.*;

import javax.persistence.*;

@Entity(name = "member")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    // 저장
    public static MemberEntity saveMember(RequestMemberDTO member, String password) {
        return MemberEntity.builder()
                .email(member.getEmail())
                .memberPw(password)
                .memberName(member.getMemberName())
                .nickName(member.getNickName())
                .memberRole(Role.USER)
                .address(AddressEntity.builder()
                        .memberAddr(member.getMemberAddress().getMemberAddr() == null
                                ? null : member.getMemberAddress().getMemberAddr())
                        .memberAddrDetail(member.getMemberAddress().getMemberAddrDetail() == null
                                ? null : member.getMemberAddress().getMemberAddrDetail())
                        .memberZipCode(member.getMemberAddress().getMemberZipCode() == null
                                ? null : member.getMemberAddress().getMemberZipCode())
                        .build()).build();
    }

    public void updateMember(ModifyMemberDTO updateMember, String encodePw) {
        this.memberPw = updateMember.getMemberPw() == null ? this.memberPw : encodePw;
        this.nickName = updateMember.getNickName() == null ? this.nickName : updateMember.getNickName();

        // 기존 주소 엔티티를 직접 수정
        if (updateMember.getMemberAddress() != null) {
            this.address = AddressEntity.builder()
                    .memberAddr(updateMember.getMemberAddress().getMemberAddr())
                    .memberAddrDetail(updateMember.getMemberAddress().getMemberAddrDetail())
                    .memberZipCode(updateMember.getMemberAddress().getMemberZipCode())
                    .build();
        } else {
            this.address = null;
        }
    }
}
