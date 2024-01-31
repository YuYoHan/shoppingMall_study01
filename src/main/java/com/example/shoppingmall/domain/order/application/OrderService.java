package com.example.shoppingmall.domain.order.application;

import com.example.shoppingmall.domain.cart.entity.CartItemEntity;
import com.example.shoppingmall.domain.cart.entity.CartStatus;
import com.example.shoppingmall.domain.cart.repository.CartItemRepository;
import com.example.shoppingmall.domain.cart.repository.CartRepository;
import com.example.shoppingmall.domain.item.entity.ItemEntity;
import com.example.shoppingmall.domain.item.entity.ItemSellStatus;
import com.example.shoppingmall.domain.item.exception.ItemException;
import com.example.shoppingmall.domain.item.repository.ItemRepository;
import com.example.shoppingmall.domain.member.entity.MemberEntity;
import com.example.shoppingmall.domain.member.exception.UserException;
import com.example.shoppingmall.domain.member.repository.MemberRepository;
import com.example.shoppingmall.domain.order.dto.RequestOrderDTO;
import com.example.shoppingmall.domain.order.dto.ResponseOrderDTO;
import com.example.shoppingmall.domain.order.dto.ResponseOrderItemDTO;
import com.example.shoppingmall.domain.order.entity.OrderEntity;
import com.example.shoppingmall.domain.order.entity.OrderItemEntity;
import com.example.shoppingmall.domain.order.exception.OrderException;
import com.example.shoppingmall.domain.order.repository.OrderItemRepository;
import com.example.shoppingmall.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class OrderService {
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;


    // 구매 확정 메소드
    public ResponseEntity<?> orderItem(List<RequestOrderDTO> orderDTOList,
                                       UserDetails userDetails) {
        try {
            // 권한 가져오기
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            String authority = authorities.iterator().next().getAuthority();
            // 접속 이메일
            String email = userDetails.getUsername();
            MemberEntity findUser = memberRepository.findByEmail(email);
            log.info("유저 : " + findUser);

            // 구매하려는 상품템리스트
            List<ResponseOrderDTO> itemList = new ArrayList<>();
            ItemEntity findItem = null;
            CartItemEntity findCartItem = null;

            if (authority.equals("ROLE_ADMIN")) {
                for (RequestOrderDTO order : orderDTOList) {
                    findItem = itemRepository.findByItemId(order.getItemId());
                    // 예외처리
                    validate(order, findItem, findUser);
                    // 장바구니 상품 조회
                    findCartItem = cartItemRepository.findByItem_ItemId(order.getItemId());

                    // 주문할 상품을 담기 위해서 주문 정보에 장바구니 상품정보를 담아주고
                    // 상품에는 가격, 예약자, 예약 수량이 있으니 그거로 처리해줍니다.
                    createOrderAndOrderItem(findCartItem, findUser, findItem, itemList);
                }
                if (findItem != null) {
                    // 상품 및 장바구니 상품 상태 변경
                    changeItemAndCartItemStatus(orderDTOList, findItem);
                } else {
                    throw new ItemException("상품이 없습니다.");
                }

                return ResponseEntity.ok().body(itemList);
            }
            throw new UserException("권한이 없습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 주문 생성과 주문 상품 생성
    private void createOrderAndOrderItem(CartItemEntity findCartItem, MemberEntity findUser, ItemEntity findItem, List<ResponseOrderDTO> itemList) {
        OrderItemEntity orderItem =
                OrderItemEntity.saveOrderItem(findCartItem);
        // 주문 생성
        OrderEntity orderEntity = OrderEntity.createOrder(findUser, findItem, orderItem);
        // DB에 저장
        OrderEntity saveOrder = orderRepository.save(orderEntity);

        ResponseOrderDTO orderDTO = ResponseOrderDTO.createOrder(saveOrder);
        itemList.add(orderDTO);
    }

    // 예외처리
    private static void validate(RequestOrderDTO order, ItemEntity findItem, MemberEntity findUser) {
        if (findItem.getItemSellStatus() != ItemSellStatus.RESERVED) {
            //throw 예약된 물품이 아니라 판매 못함
            throw new ItemException("예약된 물품이 아니라 구매처리 할 수 없습니다.");
        }

        if (!findItem.getItemReserver().equals(findUser.getEmail())) {
            //throw 구매자와 예약한사람이 달라 판매 못함
            throw new ItemException("예약자와 현재 구매하려는 사람이 달라 구매처리 할 수 없습니다.");
        }
    }

    // 상품 및 장바구니 상품 상태 변경
    private void changeItemAndCartItemStatus(List<RequestOrderDTO> orderDTOList,
                                             ItemEntity findItem) {
        // 상품 상태 변경
        List<Long> collect = orderDTOList.stream()
                .map(RequestOrderDTO::getItemId)
                .collect(Collectors.toList());

        if (Objects.requireNonNull(findItem).getStockNumber() == 0) {
            itemRepository.updateCartItemsStatus(ItemSellStatus.SOLD_OUT, collect);
        } else if (findItem.getStockNumber() > 0) {
            itemRepository.updateCartItemsStatus(ItemSellStatus.SELL, collect);
        }

        // 장바구니 상품 상태 변경
        List<Long> collect2 = orderDTOList.stream()
                .map(RequestOrderDTO::getCartItemId)
                .collect(Collectors.toList());

        cartItemRepository.updateCartItemsStatus(CartStatus.PURCHASED, collect2);
    }

    @Transactional(readOnly = true)
    public Page<ResponseOrderItemDTO> getOrders(Pageable pageable,
                                                UserDetails userDetails) {
        try {
            String email = userDetails.getUsername();
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            String authority = authorities.iterator().next().getAuthority();

            // 회원이 자기 주문 내역을 확인할 때
            MemberEntity findUser = memberRepository.findByEmail(email);

            if(findUser.getEmail().equals(email) || authority.equals("ROLE_ADMIN")) {
                OrderEntity findOrder = orderRepository.findByOrderMember_MemberId(findUser.getMemberId());
                Page<OrderItemEntity> findOrderItems =
                        orderItemRepository.findByOrder_OrderId(pageable, findOrder.getOrderId());

                return findOrderItems.map(ResponseOrderItemDTO::changeDTO);
            }
            throw new OrderException("조건에 맞지 않습니다.");
        } catch (Exception e) {
            throw new OrderException(e.getMessage());
        }
    }


}
