package com.shop.farmmunity.domain.item.repository;

import com.shop.farmmunity.domain.item.dto.ItemClassifyDto;
import com.shop.farmmunity.domain.item.dto.ItemSearchDto;
import com.shop.farmmunity.domain.item.dto.MainItemDto;
import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.itemKeyword.entity.ItemKeyword;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface  ItemRepositoryCustom {
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<Item> getMyItemPage(ItemSearchDto itemSearchDto, Pageable pageable, String email);

    Page<MainItemDto> getMainItemPage(ItemClassifyDto itemClassifyDto, ItemSearchDto itemSearchDto, Pageable pageable);
}
