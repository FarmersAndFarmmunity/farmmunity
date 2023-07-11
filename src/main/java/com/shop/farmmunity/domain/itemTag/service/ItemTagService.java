package com.shop.farmmunity.domain.itemTag.service;

import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.itemTag.entity.ItemTag;
import com.shop.farmmunity.domain.itemTag.repository.ItemTagRepository;
import com.shop.farmmunity.domain.itemKeyword.entity.ItemKeyword;
import com.shop.farmmunity.domain.itemKeyword.service.ItemKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemTagService {
    private final ItemKeywordService itemKeywordService;
    private final ItemTagRepository itemTagRepository;

    @Transactional
    public void applyItemTags(Item item, String itemTagContents) {
        List<String> itemKeywordContents = Arrays.stream(itemTagContents.split("#"))
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());

        Set<ItemTag> newItemTags = itemKeywordContents
                .stream()
                .map(itemKeywordContent -> saveItemTag(item, itemKeywordContent))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        item.updateItemTags(newItemTags);
    }

    private ItemTag saveItemTag(Item item, String itemKeywordContent) {
        ItemKeyword itemKeyword = itemKeywordService.save(itemKeywordContent);

        Optional<ItemTag> opItemTag = itemTagRepository.findByIdAndItemKeywordId(item.getId(), itemKeyword.getId());

        if (opItemTag.isPresent()) {
            return opItemTag.get();
        }

        ItemTag itemTag = ItemTag.builder()
                .item(item)
                .itemKeyword(itemKeyword)
                .build();

        itemTagRepository.save(itemTag);

        return itemTag;
    }
}
