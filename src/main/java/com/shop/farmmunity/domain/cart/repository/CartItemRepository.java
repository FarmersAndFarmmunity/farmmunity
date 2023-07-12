package com.shop.farmmunity.domain.cart.repository;

import com.shop.farmmunity.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, CartItemRepositoryCustom {
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);
}
