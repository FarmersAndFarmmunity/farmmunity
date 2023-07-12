package com.shop.farmmunity.domain.itemTag.repository;

import com.shop.farmmunity.domain.itemTag.entity.ItemTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemTagRepository extends JpaRepository<ItemTag, Long> {
        Optional<ItemTag> findByItemIdAndItemKeywordId(Long Id, Long keywordId);

        List<ItemTag> findAllByItemIdIn(long[] ids);

        List<ItemTag> findAllByItemKeyword_contentOrderByItem_idDesc(String itemKeywordContent);
}
