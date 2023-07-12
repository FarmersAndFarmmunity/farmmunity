package com.shop.farmmunity.domain.itemKeyword.entity;

import com.shop.farmmunity.base.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class ItemKeyword extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    public Object getListUrl() {
        return "/item/tag/" + content;
    }

    @Transient
    @Builder.Default
    private Map<String, Object> extra = new LinkedHashMap<>();
}
