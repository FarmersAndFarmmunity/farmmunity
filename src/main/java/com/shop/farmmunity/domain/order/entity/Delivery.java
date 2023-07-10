package com.shop.farmmunity.domain.order.entity;

import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.order.constant.Customer;
import com.shop.farmmunity.domain.order.constant.Recipient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "delivery")
@Getter
@Setter
@ToString
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Customer customer;
    @Embedded
    private Recipient recipient;
}