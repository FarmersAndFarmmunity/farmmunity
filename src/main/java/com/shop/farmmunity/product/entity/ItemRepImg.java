package com.shop.farmmunity.product.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "item_rep_img")
@Getter
@Setter
public class ItemRepImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_rep_img_id")
    private Long id;
}
