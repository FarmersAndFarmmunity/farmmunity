package com.shop.farmmunity.domain.cart.repository;

import com.shop.farmmunity.domain.cart.dto.CartDetailDto;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepositoryCustom {
    List<CartDetailDto> findCartDetailDtoList(@Param("cartId") Long cartId);
}
