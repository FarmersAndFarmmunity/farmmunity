package com.shop.farmmunity.domain.order.dto;

import com.shop.farmmunity.domain.order.constant.OrderStatus;
import com.shop.farmmunity.domain.order.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderHistDto {

    public OrderHistDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();
        this.isGroupBuying = order.isGroupBuying();
    }

    private Long orderId; // 주문아이디

    private String orderDate; // 주문 날짜

    private OrderStatus orderStatus; // 주문 상태

    private boolean isGroupBuying; // 해당 주문의 공동구매 여부

    private List<OrderItemDto> orderItemDtoList = new ArrayList<>(); //주문상품리스트

    public void addOrderItemDto(OrderItemDto orderItemDto) {
        orderItemDtoList.add(orderItemDto);
    }

}