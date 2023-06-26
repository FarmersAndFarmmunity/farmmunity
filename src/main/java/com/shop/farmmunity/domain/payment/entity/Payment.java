package com.shop.farmmunity.domain.payment.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.shop.farmmunity.base.baseEntity.BaseEntity;
import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.order.entity.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "payment")
@Getter
@Setter
@ToString
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(nullable = false)
    private String payType; // 결제 수단

    @Column(nullable = false)
    private String orderName; // 주문 이름

    @Column(nullable = false)
    private Long amount; // 결제 금액

    @Column(nullable = false)
    private Long orderId; // 주문 번호

    @Column(nullable = false)
    private String customerEmail; // 주문자 이메일

    @Column(nullable = false)
    private String customerName; // 주문자 성함

    private String paymentKey; // 결제 고유 번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static Payment createPayment(Order order, JsonNode jsonNode) {
        Payment payment = new Payment();
        payment.setPayType(jsonNode.get("method").asText());
        payment.setPaymentKey(jsonNode.get("paymentKey").asText());
        payment.setAmount(order.getTotalPrice());
        payment.setCustomerName(order.getMember().getUsername());
        payment.setCustomerEmail(order.getMember().getEmail());
        payment.setMember(order.getMember());
        payment.setOrderId(order.getId());
        payment.setOrderName(order.makeName());
        return payment;
    }
}
