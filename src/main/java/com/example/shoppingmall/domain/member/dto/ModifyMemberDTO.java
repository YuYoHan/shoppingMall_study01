package com.example.shoppingmall.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.Pattern;

@ToString
@Getter
@NoArgsConstructor
public class ModifyMemberDTO {
    @Pattern(regexp = "^[a-zA-Z가-힣]*$", message = "사용자이름은 영어와 한글만 가능합니다.")
    private String nickName;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,15}", message = "비밀번호는 영문 소문자와 숫자, 특수기호가 적어도 1개 이상씩 포함된 8 ~15자의 비밀번호여야 합니다." )
    private String memberPw;

    private AddressDTO memberAddress;

    @Builder
    public ModifyMemberDTO(String nickName, String memberPw, AddressDTO memberAddress) {
        this.nickName = nickName;
        this.memberPw = memberPw;
        this.memberAddress = memberAddress;
    }
}
