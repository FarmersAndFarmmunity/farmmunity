package com.shop.farmmunity.domain.item.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "group_buying")
@Getter
@Setter
@ToString
public class GroupBuying {

    @Id
    @Column(name = "group_buying_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 공동구매 코드

    private int discount; // 할인 가격
}
