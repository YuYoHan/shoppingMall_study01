package com.example.shoppingmall.entity;

import com.example.shoppingmall.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name="item")
public class Item {

    @Id
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    // 상품코드
    private long id;
    @Column(nullable = false, length = 50)
    // 상품명
    private String itemNm;
    @Column(name = "price", nullable = false)
    // 가격
    private int price;
    @Column(nullable = false)
    // 재고수량
    private int stockNumber;
    @Lob
    @Column(nullable = false)
    // 상품 상세 설명
    private String itemDetail;
    @Enumerated(EnumType.STRING)
    // 상품 판매 상태
    private ItemSellStatus itemSellStatus;
    // 등록 시간
    // LocalDateTime 타입은 현재 로컬 컴퓨터의 날짜와 시간을 반환
    private LocalDateTime regTime;
    // 수정 시간
    private LocalDateTime updateTime;
}
