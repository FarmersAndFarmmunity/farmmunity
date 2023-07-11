package com.shop.farmmunity.domain.itemTag.entity;

import com.shop.farmmunity.base.baseEntity.BaseEntity;
import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.itemKeyword.entity.ItemKeyword;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class ItemTag extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Item item;

    @ManyToOne
    @ToString.Exclude
    private Member member;

    @ManyToOne
    @ToString.Exclude
    private ItemKeyword itemKeyword;
}
