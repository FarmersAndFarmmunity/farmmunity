package com.shop.farmmunity.domain.order.entity;

import com.shop.farmmunity.base.baseEntity.BaseEntity;
import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.order.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate;  // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;  // 주문상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_list_id")
    private Delivery delivery;


    private boolean isPaid; // 결제 여부

    private boolean isGroupBuying; // 해당 주문건의 공동구매 여부

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList, boolean isGroupBuying) {
        Order order = new Order();
        order.setMember(member);
        for (OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        order.setGroupBuying(isGroupBuying);
        return order;
    }

    public Long getTotalPrice() {
        long totalPrice = 0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    public void cancelOrder() {
        this.orderStatus = OrderStatus.CANCEL;
        for (OrderItem orderItem : orderItems) {
            orderItem.cancel(); // 재고 수복
        }
    }

    public void payDone() {

        for (OrderItem orderItem : orderItems) {
            orderItem.payDone(); // 재고 차감
        }
        this.setPaid(true);
    }

    public String makeName() {
        String name = orderItems.get(0).getItem().getItemNm();

        if (orderItems.size() > 1) {
            name += " 외 %d건".formatted(orderItems.size() - 1);
        }

        return name;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }
}