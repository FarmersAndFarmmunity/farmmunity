package com.shop.farmmunity.domain.itemKeyword.service;

import com.shop.farmmunity.domain.itemKeyword.entity.ItemKeyword;
import com.shop.farmmunity.domain.itemKeyword.repository.ItemKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemKeywordService {
    private final ItemKeywordRepository itemKeywordRepository;

    public ItemKeyword save(String content) {
        Optional<ItemKeyword> optKeyword = itemKeywordRepository.findByContent(content);

        if (optKeyword.isPresent()) {
            return optKeyword.get();
        }

        ItemKeyword itemKeyword = ItemKeyword
                .builder()
                .content(content)
                .build();

        itemKeywordRepository.save(itemKeyword);

        return itemKeyword;
    }

    public Optional<ItemKeyword> findById(long id) {
        return itemKeywordRepository.findById(id);
    }
}
