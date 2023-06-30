package com.shop.farmmunity.domain.item.service;

import com.shop.farmmunity.domain.item.entity.ItemOption;
import com.shop.farmmunity.domain.item.repository.ItemOptionRepository;

public class ItemOptionService {

    private ItemOptionRepository itemOptionRepository;

    public void saveItemOption(ItemOption itemOption) {
        itemOptionRepository.save(itemOption);
    }
}
