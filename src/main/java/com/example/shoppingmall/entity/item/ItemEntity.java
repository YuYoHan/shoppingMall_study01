package com.example.shoppingmall.entity.item;

import com.example.shoppingmall.entity.base.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class ItemEntity extends BaseEntity {
    @Id @GeneratedValue
    private Long id;            // 상품 코드
    private String itemNum;     // 상품 명

}
