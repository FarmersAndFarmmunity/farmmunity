package com.shop.farmmunity.domain.shipping.entity;

import com.shop.farmmunity.base.baseEntity.BaseTimeEntity;
import com.shop.farmmunity.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Entity
@Table(name = "shipping")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Shipping extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipping_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String recipient;

    private String zipcode;

    private String addr;

    private String addr_detail;

    private String contact;

    private Boolean is_default;
}
