package com.shop.farmmunity.domain.item.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "group_buying")
@Getter
@Setter
@ToString
public class GroupBuying {
    @Id

    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(name = "group_buying_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 공동구매 코드

    private int discount; // 할인 가격
}
