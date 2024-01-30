package com.example.shoppingmall.domain.member.dto;

import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.member.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ToString
@Getter
@NoArgsConstructor
public class ResponseMemberDTO {
    private Long memberId;

    @NotNull(message = "이메일은 필수 입력입니다.")
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotNull(message = "이름은 필수 입력입니다.")
    private String memberName;

    @NotNull(message = "닉네임은 필수 입력입니다.")
    private String nickName;

    @NotNull(message = "권한정보는 필수 입력입니다.")
    private Role memberRole;

    private AddressDTO memberAddress;

    @Builder
    public ResponseMemberDTO(Long memberId,
                             String email,
                             String memberName,
                             String nickName,
                             Role memberRole,
                             AddressDTO memberAddress) {
        this.memberId = memberId;
        this.email = email;
        this.memberName = memberName;
        this.nickName = nickName;
        this.memberRole = memberRole;
        this.memberAddress = memberAddress;
    }

    public static ResponseMemberDTO changeDTO(MemberEntity member) {
        return ResponseMemberDTO.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
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
