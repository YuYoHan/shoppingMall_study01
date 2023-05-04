package com.example.shoppingmall.entity;

import com.example.shoppingmall.constant.Role;
import com.example.shoppingmall.dto.MemberDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name = "member")
@Getter @Setter
@ToString
public class MemberEntity {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String addr;

    // 자바의 enum 타입을 엔티티의 속성으로 저장할 수 있습니다.
    // Enum을 사용할 때 기본적으로 순서가 저장되는데, enum의 순서가
    // 바뀔 경우 문제가 발생할 수 있으니 "EnumType.STRING" 옵션을 사용해서
    // String으로 저장하기를 권장하는 겁니다.
    @Enumerated(EnumType.STRING)
    private Role role;

    // Member 엔티티를 생성하는 메소드입니다.
    // Member 엔티티에 회원을 생성하는 메소드를
    // 만들어서 관리하면 코드가 변경되더라도 한 군데만을 수정하면 됩니다.
    public  static  MemberEntity createMember(MemberDTO memberDTO, PasswordEncoder passwordEncoder) {
        MemberEntity member = new MemberEntity();
        member.setName(memberDTO.getName());
        member.setEmail(memberDTO.getEmail());
        // 스프링 시큐리티 설정 클래스에 등록한 BCryptPasswordEncoder Bean을
        // 파라미터로 넘겨서 비밀번호를 암호화합니다.
        String password = passwordEncoder.encode(memberDTO.getPassword());
        member.setPassword(password);
        member.setAddr(memberDTO.getAddr());
        member.setRole(Role.ADMIN);

        return member;
    }
}
