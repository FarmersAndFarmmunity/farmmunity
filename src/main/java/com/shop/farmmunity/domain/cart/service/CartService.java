package com.shop.farmmunity.domain.cart.service;

import com.shop.farmmunity.domain.cart.dto.CartDetailDto;
import com.shop.farmmunity.domain.cart.dto.CartItemDto;
import com.shop.farmmunity.domain.cart.dto.CartOrderDto;
import com.shop.farmmunity.domain.cart.entity.Cart;
import com.shop.farmmunity.domain.cart.entity.CartItem;
import com.shop.farmmunity.domain.cart.repository.CartItemRepository;
import com.shop.farmmunity.domain.cart.repository.CartRepository;
import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.item.repository.ItemRepository;
import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.member.repository.MemberRepository;
import com.shop.farmmunity.domain.order.dto.OrderDto;
import com.shop.farmmunity.domain.order.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    // 장바구니 담기 로직
    public Long addCart(CartItemDto cartItemDto, String email) {
        // 먼저 장바구니에 담을 아이템이 있는지 조회
        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email); // 현재 로그인한 멤버

        Cart cart = cartRepository.findByMemberId(member.getId());

        if (cart == null) { // 일단 장바구니가 없으면 하나 만들기
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        // 상품이 장바구니에 들어가 있는지 조회
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        if (savedCartItem != null) { // 장바구니가 비어 있지 않으면
            savedCartItem.addCount(cartItemDto.getCount());
            return savedCartItem.getId();
        } else {
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount(), cartItemDto.getItemOptionId());

            cartItemRepository.save(cartItem);

            return cartItem.getId();
        }
    }

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) { // 장바구니 목록을 가져옴

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>(); // 빈 장바구니 리스트를 생성

        Member member = memberRepository.findByEmail(email); // 현재 로그인한 멤버 정보를 가져옴
        Cart cart = cartRepository.findByMemberId(member.getId()); // 현재 멤버의 아이디 정보와 일치하는 장바구니를 조회

        if (cart == null) { // 없으면 빈 리스트를 반환
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());

        return cartDetailDtoList;
    }

    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email) {

        Member curMember = memberRepository.findByEmail(email); // 현재 로그인한 사용자

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

        Member savedMember = cartItem.getCart().getMember();

        // 현재 멤버와 cartItem 의 멤버와 일치하는지
        return StringUtils.equals(curMember.getEmail(), savedMember.getEmail());
    }

    // cartItem 의 수량도 함께 업데이트
    public void updateCartItemCount(Long cartItemId, int count) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    // 장바구니 목록 삭제
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

        cartItemRepository.delete(cartItem);
    }

    // 장바구니에서 주문을 하였을 때 주문 로직으로 전달이 되면 장바구니의 주문 목록을 삭제해야 함
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email) { // 사용자가 가진 장바구니의 모든 장바구니 목록을 가져와야 한다.
        List<OrderDto> orderDtoList = new ArrayList<>(); // 주문 정보를 저장할 리스트

        for (CartOrderDto cartOrderDto : cartOrderDtoList) { // 장바구니에 담긴 주문 리스트를 통해 주문 생성
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new); // 실제 카트 상품을 가져옴

            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDto.setItemOptionId(cartItem.getItemOptionId());

            orderDtoList.add(orderDto);
        }

        // 장바구니의 물건을 주문하기 했을 때 실제 주문 서비스에서 처리가 되어야 함 -> orderDtoList 를 사용
        Long orderId = orderService.orders(orderDtoList, email);// 장바구니의 상품들을 주문 로직으로 넘겨줘야 함

        // 주문 로직으로 넘긴 뒤 장바구니에서 주문한 목록들은 db 에서 삭제
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem); // 장바구니에서 삭제
        }

        return orderId;
    }
}

