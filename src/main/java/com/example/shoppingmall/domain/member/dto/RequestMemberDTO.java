package com.example.shoppingmall.domain.member.dto;

import com.example.shoppingmall.domain.member.entity.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@ToString
@NoArgsConstructor
public class RequestMemberDTO {
    @NotNull(message = "이메일은 필수 입력입니다.")
    @Pattern(regexp = "^(?:\\w+\\.?)*\\w+@(?:\\w+\\.)+\\w+$", message = "이메일 형식이 올바르지 않습니다.")
    @Email(message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotNull(message = "이름은 필수 입력입니다.")
    private String memberName;

    @NotNull(message = "닉네임은 필수 입력입니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣]*$", message = "사용자이름은 영어와 한글만 가능합니다.")
    private String nickName;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,20}", message = "비밀번호는 영문 소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8 ~20자의 비밀번호여야 합니다." )
    private String memberPw;

    @NotNull(message = "권한정보는 필수 입력입니다.")
    private Role memberRole;

    private AddressDTO memberAddress;

    @Builder
    public RequestMemberDTO(String email,
                            String memberName,
                            String nickName,
                            String memberPw,
                            Role memberRole,
                            AddressDTO memberAddress) {
        this.email = email;
        this.memberName = memberName;
        this.nickName = nickName;
        this.memberPw = memberPw;
        this.memberRole = memberRole;
        this.memberAddress = memberAddress;
    }
}
