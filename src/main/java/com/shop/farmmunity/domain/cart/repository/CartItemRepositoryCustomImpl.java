package com.shop.farmmunity.domain.cart.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.farmmunity.domain.cart.dto.CartDetailDto;
import com.shop.farmmunity.domain.cart.dto.CartItemDto;
import com.shop.farmmunity.domain.cart.entity.QCartItem;
import com.shop.farmmunity.domain.item.entity.ItemImg;
import com.shop.farmmunity.domain.item.entity.QItemImg;
import jakarta.persistence.EntityManager;
import org.springframework.data.repository.query.Param;

import java.util.List;

public class CartItemRepositoryCustomImpl implements CartItemRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public CartItemRepositoryCustomImpl(EntityManager em){ this.queryFactory = new JPAQueryFactory(em); }


    @Override
    public List<CartDetailDto> findCartDetailDtoList(@Param("cartId") Long cartId){

        List<CartDetailDto> cartDetailDtoList = queryFactory
                .select(Projections.constructor(CartDetailDto.class, QCartItem.cartItem.id, QCartItem.cartItem.item.itemNm, QCartItem.cartItem.item.price, QCartItem.cartItem.count, QItemImg.itemImg.imgUrl))
                .from(QCartItem.cartItem, QItemImg.itemImg)
                .join(QCartItem.cartItem.item)
                .where(QCartItem.cartItem.cart.id.eq(cartId),
                        QItemImg.itemImg.item.id.eq(QCartItem.cartItem.item.id),
                        QItemImg.itemImg.repImgYn.eq("Y"))
                .orderBy(QCartItem.cartItem.regTime.desc())
                .fetch();

        return cartDetailDtoList;
    }
}