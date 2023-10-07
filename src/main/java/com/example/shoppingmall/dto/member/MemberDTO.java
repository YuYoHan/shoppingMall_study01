package com.example.shoppingmall.dto.member;

import com.example.shoppingmall.dto.member.embedded.AddressDTO;
import com.example.shoppingmall.entity.member.MemberEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@ToString
@Getter
@NoArgsConstructor
public class MemberDTO {
    @Schema(description = "유저 번호", example = "1", required = true)
    private Long userId;

    @Schema(description = "이메일", example = "abc@gmail.com", required = true)
    @NotNull(message = "이메일은 필수 입력입니다.")
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String userEmail;

    @Schema(description = "회원 이름")
    @NotNull(message = "이름은 필수입력입니다.")
    private String userName;

    @Schema(description = "닉네임")
    @NotNull(message = "닉네임은 필수입력입니다.")
    @Column(unique = true)
    private String nickName;

    @Schema(description = "회원 비밀번호")
    private String userPw;

    @Schema(description = "회원 권한")
    @NotNull(message = "유저 타입은 필 수 입니다.")
    private Role role;

    @Schema(description = "소셜 로그인")
    private String provider;        // 예) google

    @Schema(description = "소셜 로그인 식별 아이디")
    private String providerId;

    @Schema(description = "회원 주소")
    private AddressDTO addressDTO;

    @Builder
    public MemberDTO(Long userId,
                     String userEmail,
                     String nickName,
                     String userName,
                     String userPw,
                     String provider,
                     String providerId,
                     Role role,
                     AddressDTO addressDTO) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName;
        this.userPw = userPw;
        this.nickName = nickName;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
        this.addressDTO = addressDTO;
    }

    public static MemberDTO toMemberDTO(MemberEntity member) {
        return MemberDTO.builder()
                .userId(member.getUserId())
                .userEmail(member.getUserEmail())
                .userName(member.getUserName())
                .userPw(member.getUserPw())
                .nickName(member.getNickName())
                .role(member.getRole())
                .provider(member.getProvider())
                .providerId(member.getProviderId())
                .addressDTO(AddressDTO.builder()
                        .userAddr(member.getAddress().getUserAddr())
                        .userAddrDetail(member.getAddress().getUserAddrDetail())
                        .userAddrEtc(member.getAddress().getUserAddrEtc())
                        .build())
                .build();
    }
}
