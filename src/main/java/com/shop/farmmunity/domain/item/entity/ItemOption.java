package com.shop.farmmunity.domain.item.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "item_option")
@Getter
@Setter
@ToString
public class ItemOption {
    @Id
    @Column(name = "item_option_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 옵션 코드

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private String optionName; // 옵션 이름

    private int extraAmount; // 추가 가격
}
