package com.example.shoppingmall.domain.admin.dto;

import com.example.shoppingmall.domain.member.dto.AddressDTO;
import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.member.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@ToString
@Getter
public class ResponseAdminDTO {
    private Long memberId;

    @NotNull(message = "이메일은 필수 입력입니다.")
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotNull(message = "이름은 필수 입력입니다.")
    private String memberName;

    @NotNull(message = "닉네임은 필수 입력입니다.")
    private String nickName;

    private String memberPw;

    @NotNull(message = "권한정보는 필수 입력입니다.")
    private Role memberRole;

    private AddressDTO memberAddress;

    @Builder
    public ResponseAdminDTO(Long memberId,
                             String email,
                             String memberName,
                             String nickName,
                             String memberPw,
                             Role memberRole,
                             AddressDTO memberAddress) {
        this.memberId = memberId;
        this.email = email;
        this.memberName = memberName;
        this.nickName = nickName;
        this.memberPw = memberPw;
        this.memberRole = memberRole;
        this.memberAddress = memberAddress;
    }
    // 엔티티를 DTO로 반환
    public static ResponseAdminDTO toMemberDTO(MemberEntity member) {
        return ResponseAdminDTO.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .memberPw(member.getMemberPw())
                .nickName(member.getNickName())
                .memberName(member.getMemberName())
                .memberRole(member.getMemberRole())
                .memberAddress(AddressDTO.builder()
                        .memberAddr(member.getAddress() == null
                                ? null : member.getAddress().getMemberAddr())
                        .memberAddrDetail(member.getAddress() == null
                                ? null : member.getAddress().getMemberAddrDetail())
                        .memberZipCode(member.getAddress() == null
                                ? null : member.getAddress().getMemberZipCode())
                        .build()).build();
    }
}
