package com.example.shoppingmall.domain.cart.application;

import com.example.shoppingmall.domain.cart.dto.*;
import com.example.shoppingmall.domain.cart.entity.CartEntity;
import com.example.shoppingmall.domain.cart.entity.CartItemEntity;
import com.example.shoppingmall.domain.cart.entity.CartStatus;
import com.example.shoppingmall.domain.cart.exception.CartException;
import com.example.shoppingmall.domain.cart.repository.CartItemQuerydslRepository;
import com.example.shoppingmall.domain.cart.repository.CartItemRepository;
import com.example.shoppingmall.domain.cart.repository.CartRepository;
import com.example.shoppingmall.domain.item.entity.ItemEntity;
import com.example.shoppingmall.domain.item.entity.ItemSellStatus;
import com.example.shoppingmall.domain.item.exception.ItemException;
import com.example.shoppingmall.domain.item.repository.ItemRepository;
import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.member.repository.MemberRepository;
import com.example.shoppingmall.global.error.OutOfStockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl {
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final CartItemQuerydslRepository cartItemQuerydslRepository;

    // 장바구니에 상품 담기
    public ResponseEntity<?> addCartItem(CreateCartDTO cart, String memberEmail) {
        try {
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);

            // 기존의 장바구니 확인
            CartEntity findCart = cartRepository.findByMemberMemberId(findUser.getMemberId());

            // 상품 재고 확인
            ItemEntity findItem = checkItemStock(cart.getItemId(), cart.getCount());

            CartEntity saveCart;
            ResponseCartDTO responseCartDTO = null;
            // 기존의 장바구니가 있다면
            if (findCart != null) {
                // 장바구니 아이템 체크
                // 장바구니의 id와 받아온 상품 id를 매개변수로 장바구니 상품을 조회
                CartItemEntity findCartItem = cartItemRepository.findCartItem(findCart.getCartId(), cart.getItemId());

                // 기존에 있는 장부구니에 추가
                if (findCartItem != null) {
                    // 예약된 상품을 장바구니에 담을 때
                    // 기존의 장바구니 중 예약된 상품과 같은 상품을 담으려고 할 때
                    if (findCartItem.getStatus().equals(CartStatus.RESERVED)
                            && findCartItem.getItem().getItemId().equals(cart.getItemId())) {
                        throw new CartException("이미 예약되어있는 상품을 추가하셨습니다");
                    }
                    // 수량 및 가격 증가
                    findCartItem.addCartItemPrice(cart);
                    // 장바구니의 장바구니 상품 리스트에 추가
                    findCart.addCartItems(findCartItem);
                    // 장바구니의 전체 가격 증가
                    findCart.totalPrice();

                } else {
                    // 상품 상태가 SELL인 경우만 장바구니에 담을 수 있습니다.
                    if (findItem.getItemSellStatus().equals(ItemSellStatus.SELL)) {
                        // 기존 장바구니에 상품이 없다면 장바구니 상품 생성
                        CartItemEntity saveCartItem =
                                CartItemEntity.saveCartItem(cart, findItem);
                        log.info("장바구니 상품 : " + saveCartItem);
                        // 장바구니의 장바구니 상품 리스트에 추가
                        findCart.addCartItems(saveCartItem);
                        // 장바구니의 전체 가격 증가
                        findCart.totalPrice();
                        saveCartItem.setCart(findCart);
                    }
                }
                saveCart = cartRepository.save(findCart);
                responseCartDTO = ResponseCartDTO.changeDTO(saveCart);
            }
            return ResponseEntity.ok().body(responseCartDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 상품 조회 및 재고 확인
    private ItemEntity checkItemStock(Long itemId, int count) {
        // 상품 조회
        ItemEntity findItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemException("해당 상품이 없습니다."));
        if (findItem == null) {
            throw new OutOfStockException("상품의 재고가 부족합니다.");
        }

        if (findItem.getItemSellStatus().equals(ItemSellStatus.SOLD_OUT)) {
            throw new OutOfStockException("상품이 판매 완료되었습니다.");
        }

        if (findItem.getStockNumber() < count) {
            throw new OutOfStockException("재고가 부족합니다. \n 요청 수량 : " + count +
                    "\n재고 : " + findItem.getStockNumber());
        }
        return findItem;
    }

    // 장바구니 상품 삭제
    public ResponseEntity<?> deleteCart(DeleteCartItemDTO item,
                                        String memberEmail) {
        try {
            // 회원 조회
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);

            // 삭제할 상품 id를 받아서 그거로 장바구니 상품 조회
            // 그거를 리스트에 담아준다.
            List<Long> cartItem = item.getItemId().stream()
                    .map(itemId -> cartItemRepository.findByItem_ItemId(itemId).getCartItemId())
                    .collect(Collectors.toList());

            // 장바구니 조회
            CartEntity findCart = cartRepository.findByMemberMemberId(findUser.getMemberId());

            // 장바구니가 해당 유저의 장바구니가 맞는지 체크
            if (findUser.getMemberId().equals(findCart.getMember().getMemberId())) {
                // 위에서 리스트에 담아둔 삭제할 상품id를 조회한 장바구니 상품 번호를 DB에서 삭제
                cartItem.forEach(cartItemRepository::deleteById);
                return ResponseEntity.ok().body("장바구니에서 상품을 삭제하였습니다.");
            }
            throw new CartException("장바구니에서 상품을 삭제하는데 실패하였습니다.");
        } catch (NullPointerException e) {
            throw new CartException("삭제하려고 하는 상품id나 카트번호가 잘못 되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 장바구니 상품 수정
    public ResponseEntity<?> updateCart(List<UpdateCartDTO> items, String memberEmail) {
        try {
            // 장바구니 상품 조회
            List<CartItemEntity> findCartItem = items.stream()
                    .map(item -> {
                        // 재고를 확인하고 반환 값으로 ItemEntity가 반환되는데
                        ItemEntity itemEntity = checkItemStock(item.getItemId(), item.getCount());
                        // 재고가 있고 상품이 있으면 상품 id를 장바구니 상품에 조회해보고 리스트에 담기
                        return cartItemRepository.findByItem_ItemId(itemEntity.getItemId());
                    }).collect(Collectors.toList());

            // 유저 조회
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);
            // 장바구니 조회
            CartEntity findCart = cartRepository.findByMemberMemberId(findUser.getMemberId());


            if (findUser.getMemberId().equals(findCart.getMember().getMemberId())
                    && !findCartItem.isEmpty()) {
                for (CartItemEntity cartItem : findCartItem) {
                    // 수량 및 가격 증가
                    items.forEach(cartItem::updateCart);
                    // 장바구니의 장바구니 상품 리스트에 추가
                    findCart.addCartItems(cartItem);
                    // 장바구니의 전체 가격 증가
                    findCart.totalPrice();
                }
                CartEntity saveCart = cartRepository.save(findCart);
                ResponseCartDTO responseCartDTO = ResponseCartDTO.changeDTO(saveCart);
                return ResponseEntity.ok().body(responseCartDTO);
            }
            throw new CartException("상품 수량 수정에 실패했습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 장바구니 상품 구매 예약
    public ResponseEntity<?> orderCart(List<CartOrderDTO> cartOrderDTOList,
                                       String memberEmail) {
        try {
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);
            CartEntity findCart = cartRepository.findByMemberMemberId(findUser.getMemberId());

            List<CartItemEntity> cartItems;
            if (findCart.getMember().getMemberId().equals(findUser.getMemberId())) {
                List<Long> cartItemIds = cartOrderDTOList.stream()
                        .map(CartOrderDTO::getCartItemId)
                        .collect(Collectors.toList());


                // DB에 JPQL로 직접 업데이트를 직접 처리하고 있습니다.
                // 예약 취소할 id를 받아서 조건에 맞는 데이터를 DB에서 전부 수정해줍니다.
                cartItems = cartItemRepository.updateCartItemsStatus(CartStatus.RESERVED, cartItemIds);

                for (CartItemEntity cartItem : cartItems) {
                    // 장바구니에서 상품을 예약하려고 하는데 상품 자체가 삭제되어 있으면 동작
                    if (cartItem.getItem() == null) {
                        throw new ItemException("예약 하려고 하는 상품이 판매자에 의해 삭제되었습니다.");
                    }

                    if (cartItem.getItem().getItemSellStatus().equals(ItemSellStatus.SOLD_OUT)) {
                        throw new OutOfStockException("상품이 판매 완료되었습니다.");
                    }

                    if (cartItem.getItem().getItemSellStatus().equals(ItemSellStatus.RESERVED)) {
                        throw new CartException("상품(" + cartItem.getItem().getItemId() + ", " +
                                cartItem.getItem().getItemName() + ")은 이미 예약되어 있습니다." +
                                "\n예약자 : " + cartItem.getItem().getItemReserver());
                    }

                    int itemNumber = cartItem.getItem().getStockNumber();
                    int orderCount = cartItem.getCount();
                    if (itemNumber < orderCount) {
                        throw new OutOfStockException("재고가 부족합니다. \n 요청 수량 : " + itemNumber +
                                "\n재고 : " + orderCount);
                    }
                    // 상품의 예약자와 예약 수량 수정
                    cartItem.orderItem(findUser.getEmail());
                }
                List<ResponseCartItemDTO> collect = cartItems.stream()
                        .map(ResponseCartItemDTO::changeDTO)
                        .collect(Collectors.toList());

                ResponseCartDTO cart = ResponseCartDTO.changeDTO(findCart);
                cart.addList(collect);

                return ResponseEntity.ok().body(cart);
            }
            throw new CartException("예약하려고 하는 장바구니 상품이 해당 회원의 장바구니 상품이 아닙니다.");
        } catch (NullPointerException | NoSuchElementException e) {
            throw new CartException("존재하지 않는 장바구니 상품id입니다.");
        } catch (Exception e) {
            throw new CartException("장바구니에서 상품을 예약하는데 실패하였습니다.\n" + e.getMessage());
        }
    }

    // 구매예약 상품 취소
    public ResponseEntity<?> cancelCartOrder(List<CartOrderDTO> cartOrderDTOList,
                                             String memberEmail) {
        try {
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);
            CartEntity findCart = cartRepository.findByMemberMemberId(findUser.getMemberId());

            if (findCart.getMember().getMemberId().equals(findUser.getMemberId())) {
                List<Long> cartItemIds = cartOrderDTOList.stream()
                        .map(CartOrderDTO::getCartItemId)
                        .collect(Collectors.toList());

                List<CartItemEntity> cartItems =
                        // DB에 JPQL로 직접 업데이트를 직접 처리하고 있습니다.
                        // 예약 취소할 id를 받아서 조건에 맞는 데이터를 DB에서 전부 수정해줍니다.
                        cartItemRepository.updateCartItemsStatus(CartStatus.WAIT, cartItemIds);

                // 수정한 데이터들을 받아오고 거기 안에 있는 상품 상태랑 예약자, 예약 수량을 수정합니다.
                // 여기서 save를 하지 않아도 적용되는 이유는
                // JPA에서 엔티티 상태 변화를 추적하기 위해서 영속성 컨텍스트를 사용하는데
                // 상태가 변경되면 영속성 컨텍스트가 이를 감지하고 변경된 내용을 추적하고 트랜잭션이
                // 커밋되는 시점에 DB에 자동으로 반영됩니다. 이게 더티 체킹입니다.
                // 그리고 이거는 여러개의 쿼리가 나가지 않고 하나의 쿼리로 나가는데
                // for문을 돌면서 상태 변화를 추적하는데 이 상태 변화는 커밋전에는 반영되지 않고
                // 트랜잭션이 커밋될 때 반영되서 하나의 쿼리로 모든 변경 사항이 DB에 적용됩니다.
                cartItems.forEach(cartItem -> {
                    // 장바구니에서 상품을 예약하려고 하는데 상품 자체가 삭제되어 있으면 동작
                    if (cartItem.getItem() == null) {
                        throw new ItemException("예약 하려고 하는 상품이 판매자에 의해 삭제되었습니다.");
                    }
                    cartItem.cancelOrderItem();
                });
                return ResponseEntity.ok().body("구매예약을 취소하였습니다.");
            }
            throw new CartException("예약하려고 하는 장바구니 상품이 해당 회원의 장바구니 상품이 아닙니다.");
        } catch (NullPointerException | NoSuchElementException e) {
            throw new CartException("예약취소하려고 하는 장바구니상품id가 해당 회원의 장바구니 상품이 아닙니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public Page<ResponseCartItemDTO> getCarts(Pageable pageable,
                                              String memberEmail,
                                              CartStatusCondition condition) {
        try {
            MemberEntity findUser = memberRepository.findByEmail(memberEmail);
            CartEntity findCart = cartRepository.findByMemberMemberId(findUser.getMemberId());
            if (findCart == null) {
                throw new CartException("장바구니가 없습니다.");
            }
            Page<CartItemEntity> allCart = cartItemQuerydslRepository.findAllCart(pageable, condition);
            return allCart.map(ResponseCartItemDTO::changeDTO);

        } catch (Exception e) {
            throw new CartException("정보를 가져오는 것을 실패했습니다.");
        }
    }
}
