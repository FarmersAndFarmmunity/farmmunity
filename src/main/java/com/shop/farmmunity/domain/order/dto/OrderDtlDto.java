package com.shop.farmmunity.domain.order.dto;

import com.shop.farmmunity.domain.order.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderDtlDto {

    public OrderDtlDto(Order order, String CLIENT_KEY) {
        this.orderId = order.getId();
        this.customerName = order.getMember().getUsername();
        this.customerEmail = order.getMember().getEmail();
        this.orderName = order.makeName();
        this.totalPrice = order.getTotalPrice();
        this.CLIENT_KEY = CLIENT_KEY;
    }

    private Long orderId; //주문아이디

    private String customerName; // 주문자 이름

    private String customerEmail; // 주문자 이메일

    private String orderName; // 주문명

    private List<OrderItemDtlDto> orderItemDtlDtos = new ArrayList<>(); //주문상품리스트

    private Long totalPrice; // 총주문 금액

    private String CLIENT_KEY; // 토스 페이먼츠 클라이언트 키

    public void addOrderItemDto(OrderItemDtlDto orderItemDtlDto) {
        orderItemDtlDtos.add(orderItemDtlDto);
    }

}