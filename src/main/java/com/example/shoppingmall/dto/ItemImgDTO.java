package com.example.shoppingmall.dto;

import com.example.shoppingmall.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@Setter
@ToString
public class ItemImgDTO {
    private Long Id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;


    private static ModelMapper modelMapper = new ModelMapper();

    // ItemImg 엔티티 객체를 파라미터로 받아서 ItemImg 객체의 자료형과 멤버변수의 이름이
    // 같을 때 ItemImgDTO로 값을 복사해서 반환합니다. static 메소드로 선언해
    // ItemImgDTO 객체를 생성하지 않아도 호출할 수 있도록 합니다.
    public static ItemImgDTO of(ItemImg itemImg) {
        return modelMapper.map(itemImg, ItemImgDTO.class);
    }
}
