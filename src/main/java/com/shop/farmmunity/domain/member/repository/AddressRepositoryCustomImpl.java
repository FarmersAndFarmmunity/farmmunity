package com.shop.farmmunity.domain.member.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.farmmunity.domain.member.dto.AddressDto;
import com.shop.farmmunity.domain.member.entity.Address;
import com.shop.farmmunity.domain.member.entity.QAddress;
import jakarta.persistence.EntityManager;

import java.util.List;

public class AddressRepositoryCustomImpl implements AddressRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    public AddressRepositoryCustomImpl(EntityManager em){ this.queryFactory = new JPAQueryFactory(em); }

    @Override
    public List<AddressDto> findAddressList(Long memberId) {

        List<AddressDto> addressDtoList = queryFactory
                .select(Projections.constructor(AddressDto.class, QAddress.address.id, QAddress.address.shippingName, QAddress.address.recipientName, QAddress.address.recipientContact, QAddress.address.zipcode, QAddress.address.addr, QAddress.address.addr_detail, QAddress.address.is_default))
                .from(QAddress.address)
                .where(QAddress.address.member.id.eq(memberId))
                .orderBy(QAddress.address.is_default.desc())
                .fetch();
        return addressDtoList;
    }

    @Override
    public Address findDefaultAddress(Long memberId) {

        Address address = queryFactory
                .selectFrom(QAddress.address)
                .where(QAddress.address.is_default.eq(true), QAddress.address.member.id.eq(memberId))
                .fetchFirst();
        return address;
    }
}
