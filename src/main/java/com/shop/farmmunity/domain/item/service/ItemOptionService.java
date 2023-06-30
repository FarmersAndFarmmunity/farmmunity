package com.shop.farmmunity.domain.item.service;

import com.shop.farmmunity.domain.item.entity.ItemOption;
import com.shop.farmmunity.domain.item.repository.ItemOptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemOptionService {

    private final ItemOptionRepository itemOptionRepository;

    public void saveItemOption(ItemOption itemOption) {
        itemOptionRepository.save(itemOption);
    }
}
