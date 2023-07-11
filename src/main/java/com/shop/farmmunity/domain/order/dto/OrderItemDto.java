package com.shop.farmmunity.domain.order.dto;


import com.shop.farmmunity.domain.order.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {

    public OrderItemDto(OrderItem orderItem, String imgUrl) {
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
        this.itemId = orderItem.getItem().getId();
        this.orderId = orderItem.getOrder().getId();
        this.optionNm = orderItem.getOptionNm();
    }

    private String itemNm; //상품명

    private int count; //주문수량

    private int orderPrice; //주문 금액

    private String imgUrl;  //상품 이미지 경로

    private Long itemId; // 상품 id

    private Long orderId;

    private String optionNm;
}