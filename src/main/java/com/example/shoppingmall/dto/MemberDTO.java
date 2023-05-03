package com.example.shoppingmall.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@ToString
public class MemberDTO {
    @NotBlank(message = "이메일은 필수 입력입니다.")
    private String name;

    @NotEmpty(message = "이메일은 필수 입력입니다.")
    @Email(message = "이메일 형식으로 적어주세요")
    private String email;

    @NotEmpty(message = "비밀번호는 필수입니다.")
    @Length(min = 8, max = 16, message = "비밀번호는 8자 이상, 16자 이하로 해주세요")
    private String password;

    @NotEmpty(message = "주소는 필수 입력입니다.")
    private String addr;
}
