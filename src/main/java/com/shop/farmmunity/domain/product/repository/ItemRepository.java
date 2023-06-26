package com.shop.farmmunity.domain.product.repository;

import com.shop.farmmunity.domain.product.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    void deleteById(long itemId);
}
