package com.example.shoppingmall.dto.item;

import com.example.shoppingmall.dto.member.MemberDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class CartDTO {
    private Long cartId;
    private MemberDTO member;

    @Builder
    public CartDTO(Long cartId, MemberDTO member) {
        this.cartId = cartId;
        this.member = member;
    }
}
