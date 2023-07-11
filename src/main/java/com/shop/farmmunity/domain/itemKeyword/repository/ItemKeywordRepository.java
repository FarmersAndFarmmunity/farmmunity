package com.shop.farmmunity.domain.itemKeyword.repository;

import com.shop.farmmunity.domain.itemKeyword.entity.ItemKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemKeywordRepository extends JpaRepository<ItemKeyword, Long> {
    Optional<ItemKeyword> findByContent(String keywordContent);
}
