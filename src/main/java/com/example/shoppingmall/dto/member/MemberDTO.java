package com.example.shoppingmall.dto.member;

import com.example.shoppingmall.dto.member.embedded.AddressDTO;
import com.example.shoppingmall.entity.member.MemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Optional;

@ToString
@Getter
@NoArgsConstructor
public class MemberDTO {
    private Long userId;
    @NotNull(message = "이메일은 필수 입력입니다.")
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String userEmail;

    @NotNull(message = "이름은 필수입력입니다.")
    private String userName;
    private String nickName;

    @NotNull(message = "비밀번호는 필 수 입니다.")
    private String userPw;

    @NotNull(message = "유저 타입은 필 수 입니다.")
    private Role role;

    private AddressDTO addressDTO;

    @Builder
    public MemberDTO(Long userId,
                     String userEmail,
                     String nickName,
                     String userName,
                     String userPw,
                     Role role,
                     AddressDTO addressDTO) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPw = userPw;
        this.nickName = nickName;
        this.role = role;
        this.addressDTO = addressDTO;
    }

    public static MemberDTO toMemberDTO(Optional<MemberEntity> member) {
        MemberDTO memberDTO = MemberDTO.builder()
                .userId(member.get().getUserId())
                .userEmail(member.get().getUserEmail())
                .userName(member.get().getUserName())
                .userPw(member.get().getUserPw())
                .nickName(member.get().getNickName())
                .role(member.get().getRole())
                .addressDTO(AddressDTO.builder()
                        .userAddr(member.get().getAddress().getUserAddr())
                        .userAddrDetail(member.get().getAddress().getUserAddrDetail())
                        .userAddrEtc(member.get().getAddress().getUserAddrEtc())
                        .build())
                .build();

        return memberDTO;
    }
}
