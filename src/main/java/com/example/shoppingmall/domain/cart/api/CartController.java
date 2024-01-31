package com.example.shoppingmall.domain.cart.api;

import com.example.shoppingmall.domain.cart.application.CartServiceImpl;
import com.example.shoppingmall.domain.cart.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartServiceImpl cartService;

    // 장바구니에 상품 추가
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> addCartItem(@Validated @RequestBody CreateCartDTO cartDTO,
                                         BindingResult result,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if(result.hasErrors()) {
                return ResponseEntity.badRequest().body(result.getClass().getSimpleName());
            }

            String email = userDetails.getUsername();
            return cartService.addCartItem(cartDTO, email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 장바구니 상품 수정
    @PutMapping("/items")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateCartItem(@RequestBody List<UpdateCartDTO> cartDTO,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            return cartService.updateCart(cartDTO, email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    // 장바구니 상품 삭제
    @DeleteMapping("/items")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> removeCartItem(@RequestBody DeleteCartItemDTO removeId,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            return cartService.deleteCart(removeId, email);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 장바구니 구매 예약
    @PostMapping("/orderItems")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> orderCart(@RequestBody List<CartOrderDTO> cartOrderDTOList,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            return cartService.orderCart(cartOrderDTOList, email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 장바구니 예약 취소
    @PostMapping("/cancelItems")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> cancelOrder(@RequestBody List<CartOrderDTO> cartOrderDTOList,
                                         @AuthenticationPrincipal UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            return cartService.cancelCartOrder(cartOrderDTOList, email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    // 장바구니 상품 페이지로 가져오기
    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> getCartList(Pageable pageable,
                                         @AuthenticationPrincipal UserDetails userDetails,
                                         CartStatusCondition condition) {
        try {
            String email = userDetails.getUsername();
            Page<ResponseCartItemDTO> carts = cartService.getCarts(pageable, email, condition);
            Map<String, Object> response = new HashMap<>();
            // 현재 페이지의 아이템 목록
            response.put("items", carts.getContent());
            // 현재 페이지 번호
            response.put("nowPageNumber", carts.getNumber());
            // 전체 페이지 수
            response.put("totalPage", carts.getTotalPages());
            // 한 페이지에 출력되는 데이터 개수
            response.put("pageSize", carts.getSize());
            // 다음 페이지 존재 여부
            response.put("hasNextPage", carts.hasNext());
            // 이전 페이지 존재 여부
            response.put("hasPreviousPage", carts.hasPrevious());
            // 첫 번째 페이지 여부
            response.put("isFirstPage", carts.isFirst());
            // 마지막 페이지 여부
            response.put("isLastPage", carts.isLast());

            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}
