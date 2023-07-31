package com.example.shoppingmall.dto.item;

import com.example.shoppingmall.dto.member.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class CartDTO {
    @Schema(description = "카트 번호")
    private Long cartId;
    @Schema(description = "유저 정보")
    private MemberDTO member;

    @Builder
    public CartDTO(Long cartId, MemberDTO member) {
        this.cartId = cartId;
        this.member = member;
    }
}
