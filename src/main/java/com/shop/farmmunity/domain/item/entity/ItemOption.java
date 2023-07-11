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

    private int quantity; // 옵션 수량

    private int extraAmount; // 추가 가격

    private int gbPrice; // 공동구매 가격

    public void updateItemOption(String optionName, int extraAmount, int quantity, int gbPrice) {
        this.optionName = optionName;
        this.extraAmount = extraAmount;
        this.quantity = quantity;
        this.gbPrice = gbPrice;
    }
}
