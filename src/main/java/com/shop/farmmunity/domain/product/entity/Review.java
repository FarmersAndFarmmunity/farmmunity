package com.shop.farmmunity.domain.product.entity;

import com.shop.farmmunity.base.baseEntity.BaseEntity;
import com.shop.farmmunity.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Item item;

    private String title;

    @Column(length = 300)
    private String content;

    // TODO: 리뷰에도 이미지 첨부할 수 있도록 구현하기
    @Column(length = 1000)
    private String image;

    // TODO: 별점 기능 구현 예정
    private int rate;

    public void addItem(Item item) {
        this.item = item;
    }
}
