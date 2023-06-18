package com.example.shoppingmall.entity;

import com.example.shoppingmall.constant.ItemSellStatus;
import com.example.shoppingmall.dto.ItemFormDTO;
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
public class Item extends BaseEntity {

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

    public void updateItem(ItemFormDTO itemFormDTO) {
        this.itemNm = itemFormDTO.getItemName();
        this.price = itemFormDTO.getPrice();
        this.stockNumber = itemFormDTO.getStockNumber();
        this.itemDetail = itemFormDTO.getItemDetail();
        this.itemSellStatus = itemFormDTO.getItemSellStatus();
    }
}
