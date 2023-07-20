package com.example.shoppingmall.entity;

import com.example.shoppingmall.entity.base.BaseEntity;
import com.example.shoppingmall.entity.member.MemberEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "cart")
@Getter @Setter
@ToString
public class Cart extends BaseEntity {
    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @OneToOne 어노테이션을 이용해 회원 엔티티와 일대일로 매핑합니다.
    @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn 어노테이션을 이용해 매핑할 외래키를 지정합니다.
    // name 속성에는 매핑할 외래키의 이름을 설정합니다.
    // name을 명시하지 않으면 JPA가 알아서 ID를 찾지만 컬럼명이 원하는 대로 생성되지 않을 수
    // 있기 때문에 직접 지정합니다.
    @JoinColumn(name = "member_id")
    private MemberEntity member;
}
