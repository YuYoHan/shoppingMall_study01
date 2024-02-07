package com.example.shoppingmall.domain.item.entity;

import com.example.shoppingmall.domain.container.entity.ContainerEntity;
import com.example.shoppingmall.domain.item.dto.ResponseItemDTO;
import com.example.shoppingmall.domain.item.dto.UpdateItemDTO;
import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.model.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity(name = "item")
@Getter
@ToString(exclude = {"member", "itemImgList"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ItemEntity extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long itemId;
    private String itemName;     // 상품 명
    private int price;          // 가격
    private int stockNumber;    // 재고수량
    @Column(nullable = false, name = "item_detail")
    private String itemDetail;  // 상품 상세 설명
    @Enumerated(EnumType.STRING)
    @Column(name = "item_sell_status", nullable = false)
    private ItemSellStatus itemSellStatus;  // 상품 판매 상태
    @Embedded
    @Column(name = "item_place", nullable = false)
    private ContainerEntity itemPlace;

    @Column(name="item_reserver")
    private String itemReserver;

    @Column(name="item_ramount")
    private int itemRamount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @Column(name = "item_img")
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    @Builder.Default
    // 상품 저장 후 수정할 때 상품 이미지 정보를 저장하는 리스트
    private List<ItemImgEntity> itemImgList = new ArrayList<>();


    public static ItemEntity saveEntity(ResponseItemDTO itemDTO,
                                        MemberEntity member) {
        return ItemEntity.builder()
                .itemName(itemDTO.getItemName())
                .itemDetail(itemDTO.getItemDetail())
                .itemPlace(ContainerEntity.builder()
                        .containerName(itemDTO.getSellPlace().getContainerName())
                        .containerAddr(itemDTO.getSellPlace().getContainerAddr())
                        .build())
                .itemSellStatus(itemDTO.getItemSellStatus())
                .stockNumber(itemDTO.getStockNumber())
                .price(itemDTO.getPrice())
                .itemRamount(itemDTO.getItemRamount())
                .member(member)
                .itemReserver(itemDTO.getItemReserver() == null ? null : itemDTO.getItemReserver())
                .build();
    }

    // ItemEntity에 있는 이미지 리스트에 추가
    public void addItemImgList(ItemImgEntity itemImg){
        this.itemImgList.add(itemImg);
    }

    public void updateItem(UpdateItemDTO item) {
        this.itemName = Optional.ofNullable(item.getItemName()).orElse(this.getItemName());
        this.itemDetail = Optional.ofNullable(item.getItemDetail()).orElse(this.getItemDetail());
        this.stockNumber = Optional.of(item.getStockNumber()).orElse(this.stockNumber);
        this.price = Optional.of(item.getPrice()).orElse(this.price);
    }

    // 상태만 바꿔주는 메소드
    public void changeStatus(ItemSellStatus status){
        this.itemSellStatus = status;
    }

    // 상품 구매예약 시 예약정보 셋팅
    public void reserveItem(String itemReserver, int amount){
        this.itemReserver = itemReserver;
        this.itemRamount = amount;
        // 장바구니 상품의 개수 차감
        if(itemReserver != null && amount > 0) {
        this.stockNumber -= amount;
        }
    }

    public void cancelReserveItem(int amount) {
        this.itemReserver = null;
        this.itemRamount = 0;
        if (amount > 0) {
            this.stockNumber += amount; // 장바구니 상품의 개수 증가 (주문 취소 시)
        }
    }
}
