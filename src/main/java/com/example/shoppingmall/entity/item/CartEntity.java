package com.example.shoppingmall.entity.item;

import com.example.shoppingmall.entity.base.BaseEntity;
import com.example.shoppingmall.entity.base.BaseTimeEntity;
import com.example.shoppingmall.entity.member.MemberEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name ="cart")
@ToString
@Getter
@Table
@NoArgsConstructor
public class CartEntity extends BaseTimeEntity {
    @Id @GeneratedValue
    @Column(name = "cart_id")
    private Long id;

    // 일대일 매핑
    @OneToOne(fetch = FetchType.LAZY)
    // 매핑할 외래키를 지정합니다.
    // name 에는 매핑할 외래키의 이름을 설정
    @JoinColumn(name = "member_id")
    private MemberEntity member;
}
