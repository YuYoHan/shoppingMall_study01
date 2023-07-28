package com.example.shoppingmall.dto.item;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor
public class ItemDTO {
    private Long id;            // 상품 코드
    @NotBlank(message = "상품명은 필수 입력입니다.")
    private String itemNum;     // 상품 명
    @NotNull(message = "가격은 필수 입력입니다.")
    private int price;          // 가격
    @NotNull(message = "재고 수량은 필수 입력입니다.")
    private int stockNumber;    // 재고수량
    @NotNull(message = "설명은 필수 입력입니다.")
    private String itemDetail;  // 상품 상세 설명
    private ItemSellStatus itemSellStatus;  // 상품 판매 상태
    private LocalDateTime regTime;
    private LocalDateTime updateTime;

    // 상품 저장 후 수정할 때 상품 이미지 정보를 저장하는 리스트
    private List<ItemImgDTO> itemImgList = new ArrayList<>();

    // 상품의 이미지 아이디를 저장하는 리스트입니다.
    // 상품 등록 시에는 아직 상품의 이미지를 저장하지 않았기 때문에
    // 아무 값도 들어가 있지 않고 수정 시에 이미지 아이디를 담아둘 용도로 사용합니다.
    private List<Long> itemImgIds = new ArrayList<>();

    @Builder

    public ItemDTO(Long id,
                   String itemNum,
                   int price,
                   int stockNumber,
                   String itemDetail,
                   ItemSellStatus itemSellStatus,
                   LocalDateTime regTime,
                   LocalDateTime updateTime,
                   List<ItemImgDTO> itemImgList,
                   List<Long> itemImgIds) {
        this.id = id;
        this.itemNum = itemNum;
        this.price = price;
        this.stockNumber = stockNumber;
        this.itemDetail = itemDetail;
        this.itemSellStatus = itemSellStatus;
        this.regTime = regTime;
        this.updateTime = updateTime;
        this.itemImgList = itemImgList;
        this.itemImgIds = itemImgIds;
    }
}
