package com.shop.farmmunity.domain.member.repository;

import com.shop.farmmunity.domain.member.dto.AddressDto;
import com.shop.farmmunity.domain.member.entity.Address;

import java.util.List;

public interface AddressRepositoryCustom {

    List<AddressDto> findAddressList(Long memberId);

    Address findDefaultAddress(Long memberId);
}
