package com.shop.farmmunity.domain.cart.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartOrderDto { // 주문할 상품 데이터를 전달하기 위함

    private Long cartItemId;

    private List<CartOrderDto> cartOrderDtoList;
}
