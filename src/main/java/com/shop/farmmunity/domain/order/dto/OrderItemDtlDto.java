package com.shop.farmmunity.domain.order.dto;

import com.shop.farmmunity.domain.order.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDtlDto {

    public OrderItemDtlDto(OrderItem orderItem, String imgUrl) {
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
        this.optionNm = orderItem.getOptionNm();
    }

    private String itemNm; // 상품명

    private int count; // 주문 상품 수량

    private int orderPrice; // 주문 상품 금액

    private String imgUrl;  // 상품 이미지 경로

    private String optionNm; // 옵션명

}
