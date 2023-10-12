package com.example.shoppingmall.entity.item;

import com.example.shoppingmall.dto.item.ItemSellStatus;
import com.example.shoppingmall.entity.base.BaseTimeEntity;
import com.example.shoppingmall.entity.comment.CommentEntity;
import com.example.shoppingmall.entity.member.MemberEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "item")
@Getter
@ToString
@NoArgsConstructor
public class ItemEntity extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;            // 상품 코드
    @Column(name = "item_name", nullable = false)
    private String itemName;     // 상품 명
    @Column(name = "item_price", nullable = false)
    private int price;          // 가격
    @Column(name = "stock_number",nullable = false)
    private int stockNumber;    // 재고수량
    // BLOB, CLOB 타입 매핑
    // CLOB이란 사이즈가 큰 데이터를 외부 파일로 저장하기 위한 데이터입니다.
    // 문자형 대용량 파일을 저장하는데 사용하는 데이터 타입이라고 생각하면 됩니다.
    // BLOB은 바이너리 데이터를 DB외부에 저장하기 위한 타입입니다.
    // 이미지, 사운드, 비디오 같은 멀티미디어 데이터를 다룰 때 사용할 수 있습니다.
    @Lob
    @Column(nullable = false, name = "item_detail")
    private String itemDetail;  // 상품 상세 설명

    @Enumerated(EnumType.STRING)
    @Column(name = "item_sell_stauts")
    private ItemSellStatus itemSellStatus;  // 상품 판매 상태

    // 상품 이미지
    @Column(name = "item_img")
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("itemImgId asc")
    // 상품 저장 후 수정할 때 상품 이미지 정보를 저장하는 리스트
    private List<ItemImgEntity> itemImgList = new ArrayList<>();

    // 상품 댓글
    @Column(name = "comment")
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("commentId asc")
    private List<CommentEntity> commentEntityList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @Builder
    public ItemEntity(Long itemId,
                      String itemName,
                      int price,
                      int stockNumber,
                      String itemDetail,
                      ItemSellStatus itemSellStatus,
                      List<ItemImgEntity> itemImgList,
                      List<CommentEntity> commentEntityList,
                      MemberEntity member
                      ) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.stockNumber = stockNumber;
        this.itemDetail = itemDetail;
        this.itemSellStatus = itemSellStatus;
        this.itemImgList = itemImgList;
        this.commentEntityList = commentEntityList;
        this.member = member;
    }
}
