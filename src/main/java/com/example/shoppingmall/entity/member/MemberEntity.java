package com.example.shoppingmall.entity.member;

import com.example.shoppingmall.constant.Role;
import com.example.shoppingmall.dto.MemberFormDto;
import com.example.shoppingmall.entity.base.BaseEntity;
import com.example.shoppingmall.entity.member.embedded.Address;
import groovy.transform.builder.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;

@Entity
@Table(name = "member")
@Getter
@ToString
@NoArgsConstructor
public class MemberEntity extends BaseEntity {
    @Id
    @Column(name = "member_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="user_name", nullable = false)
    private String userName;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    private String userPw;

    private String nickName;

    // 자바의 enum 타입을 엔티티의 속성으로 저장할 수 있습니다.
    // Enum을 사용할 때 기본적으로 순서가 저장되는데, enum의 순서가
    // 바뀔 경우 문제가 발생할 수 있으니 "EnumType.STRING" 옵션을 사용해서
    // String으로 저장하기를 권장하는 겁니다.
    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private Address addr;

    @Builder
    public MemberEntity(Long id,
                        String userName,
                        String userEmail,
                        String userPw,
                        String nickName,
                        Role role,
                        Address addr) {
        this.id = id;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPw = userPw;
        this.nickName = nickName;
        this.role = role;
        this.addr = addr;
    }
}
