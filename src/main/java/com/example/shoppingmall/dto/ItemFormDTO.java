package com.example.shoppingmall.dto;

import com.example.shoppingmall.constant.ItemSellStatus;
import com.example.shoppingmall.entity.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class ItemFormDTO {
    private Long id;

    @NotBlank(message = "상품명은 필수 입력입니다.")
    private String itemName;

    @NotNull(message = "가격은 필수 입력입니다.")
    private String price;

    @NotBlank(message = "이름은 필수 입력이빈다.")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력입니다.")
    private Integer stockNumber;

    private ItemSellStatus itemSellStatus;

    // 상품 저장 후 수정할 때 상품 이미지 정보를 저장하는 리스트입니다.
    private List<ItemImgDTO> itemImgDTOList = new ArrayList<>();

    // 상품의 이미지 아이디를 저장하는 리스트입니다.
    // 상품 등록 시에는 아직 상품의 이미지를 저장하지 않았기 때문에 아무 값도
    // 들어가 있지 않고 수정 시에 이미지 아이디를 담아둘 용도로 사용합니다.
    private List<Long> itemImgIds = new ArrayList<>();


    private static ModelMapper modelMapper = new ModelMapper();
    // 1
    public Item createItem() {
        return modelMapper.map(this, Item.class);
    }
    // 2
    public static ItemFormDTO of(Item item) {
        return modelMapper.map(item, ItemFormDTO.class);
    }

    /*
    *   1, 2번은 modelMapper를 이용하여 엔티티 객체와 DTO 객체 간의 데이터를 복사하여
    *   복사한 객체를 반환해주는 메소드입니다.
    * */

}
