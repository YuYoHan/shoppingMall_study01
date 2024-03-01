package com.example.shoppingmall.domain.item.dto;

import com.example.shoppingmall.domain.container.dto.ContainerDTO;
import com.example.shoppingmall.domain.item.entity.ItemEntity;
import com.example.shoppingmall.domain.item.entity.ItemImgEntity;
import com.example.shoppingmall.domain.item.entity.ItemSellStatus;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ResponseItemDTO {
    private Long itemId;
    @NotBlank(message = "상품명은 필수 입력입니다.")
    private String itemName;     // 상품 명
    @NotNull(message = "가격은 필수 입력입니다.")
    private int price;          // 가격
    @NotNull(message = "설명은 필수 입력입니다.")
    private String itemDetail;  // 상품 상세 설명
    private ItemSellStatus itemSellStatus;  // 상품 판매 상태
    private LocalDateTime regTime;
    private String memberNickName;
    private int stockNumber;    // 재고수량
    private ContainerDTO sellPlace;
    private String itemReserver;
    private int itemRamount;
    @Builder.Default
    private List<ItemImgDTO> itemImgList = new ArrayList<>();


    public static ResponseItemDTO changeDTO(ItemEntity item) {
        // 이미지 처리
        List<ItemImgEntity> itemImgList =
                item.getItemImgList() != null ? item.getItemImgList() : Collections.emptyList();
        List<ItemImgDTO> itemImgDTOList = itemImgList.stream()
                .map(ItemImgDTO::changeDTO)
                .collect(Collectors.toList());

        return ResponseItemDTO.builder()
                .itemId(item.getItemId())
                .itemName(item.getItemName())
                .price(item.getPrice())
                .stockNumber(item.getStockNumber())
                .itemDetail(item.getItemDetail())
                .itemSellStatus(item.getItemSellStatus())
                .regTime(item.getRegTime())
                .sellPlace(ContainerDTO.builder()
                        .containerName(item.getItemPlace().getContainerName())
                        .containerAddr(item.getItemPlace().getContainerAddr())
                        .build())
                .itemReserver(item.getItemReserver())
                .itemRamount(item.getItemRamount())
                .memberNickName(item.getMember().getNickName())
                .itemImgList(itemImgDTOList)
                .build();
    }

    public void setSellPlace(String placeName, String placeAddr) {
        ContainerDTO.builder()
                .containerName(placeName)
                .containerAddr(placeAddr)
                .build();
    }
}
