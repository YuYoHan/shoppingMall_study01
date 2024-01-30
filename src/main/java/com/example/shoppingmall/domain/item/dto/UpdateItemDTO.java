package com.example.shoppingmall.domain.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UpdateItemDTO {
    @NotBlank(message = "상품명은 필수 입력입니다.")
    private String itemName;     // 상품 명
    @NotNull(message = "가격은 필수 입력입니다.")
    private int price;          // 가격
    @NotNull(message = "설명은 필수 입력입니다.")
    private String itemDetail;  // 상품 상세 설명
    @NotNull(message = "재고 수량은 필수 입력입니다.")
    private int stockNumber;    // 재고수량
    private List<Long> removeImgId;
}
