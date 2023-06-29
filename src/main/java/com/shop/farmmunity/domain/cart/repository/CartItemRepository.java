package com.shop.farmmunity.domain.cart.repository;

import com.shop.farmmunity.domain.cart.dto.CartDetailDto;
import com.shop.farmmunity.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long>, CartItemRepositoryCustom {
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

}
