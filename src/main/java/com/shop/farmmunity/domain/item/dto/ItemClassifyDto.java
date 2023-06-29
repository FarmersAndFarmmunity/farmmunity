package com.shop.farmmunity.domain.item.dto;

import com.shop.farmmunity.domain.item.constant.ItemClassifyStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemClassifyDto {
    private ItemClassifyStatus itemClassifyStatus;

    public void setItemClassifyStatus(ItemClassifyStatus itemClassifyStatus) {
        this.itemClassifyStatus = itemClassifyStatus;
    }
}
