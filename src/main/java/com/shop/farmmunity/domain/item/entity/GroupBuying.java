package com.shop.farmmunity.domain.item.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "GroupBuying")
@Getter
@Setter
@ToString
public class GroupBuying {
    @Id
    @Column(name = "groupbuying_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 공동구매 코드

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private int discount; // 할인 가격
}
