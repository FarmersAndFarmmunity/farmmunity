package com.shop.farmmunity.domain.item.repository;

import com.shop.farmmunity.domain.item.entity.ItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemOptionRepository extends JpaRepository<ItemOption, Long> {
    List<ItemOption> findByItemIdOrderByIdAsc(Long itemId);
    void deleteAllByItemId(Long itemId);
}
