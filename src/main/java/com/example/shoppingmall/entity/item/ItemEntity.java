package com.example.shoppingmall.entity.item;

import com.example.shoppingmall.dto.item.ItemSellStatus;
import com.example.shoppingmall.entity.base.BaseEntity;
import com.example.shoppingmall.entity.base.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity(name = "item")
@Getter
@ToString
@NoArgsConstructor
public class ItemEntity extends BaseTimeEntity {
    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;            // 상품 코드
    @Column(nullable = false, length = 50)
    private String itemNum;     // 상품 명
    @Column(name = "price", nullable = false)
    private int price;          // 가격
    @Column(nullable = false)
    private int stockNumber;    // 재고수량
    // BLOB, CLOB 타입 매핑
    // CLOB이란 사이즈가 큰 데이터를 외부 파일로 저장하기 위한 데이터입니다.
    // 문자형 대용량 파일을 저장하는데 사용하는 데이터 타입이라고 생각하면 됩니다.
    // BLOB은 바이너리 데이터를 DB외부에 저장하기 위한 타입입니다.
    // 이미지, 사운드, 비디오 같은 멀티미디어 데이터를 다룰 때 사용할 수 있습니다.
    @Lob
    @Column(nullable = false)
    private String itemDetail;  // 상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;  // 상품 판매 상태

    @Builder
    public ItemEntity(Long id,
                      String itemNum,
                      int price,
                      int stockNumber,
                      String itemDetail,
                      ItemSellStatus itemSellStatus) {
        this.id = id;
        this.itemNum = itemNum;
        this.price = price;
        this.stockNumber = stockNumber;
        this.itemDetail = itemDetail;
        this.itemSellStatus = itemSellStatus;
    }
}
