package com.shop.farmmunity.domain.item.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.shop.farmmunity.domain.item.constant.ItemClassifyStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainItemDto {
    private Long id;
    private String itemNm;
    private String itemDetail;
    private String imgUrl;
    private Integer price;
    private ItemClassifyStatus itemClassifyStatus;

    @QueryProjection
    public MainItemDto(Long id, String itemNm, String itemDetail, String imgUrl, Integer price, ItemClassifyStatus itemClassifyStatus) {
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
        this.itemClassifyStatus = itemClassifyStatus;
    }
}
