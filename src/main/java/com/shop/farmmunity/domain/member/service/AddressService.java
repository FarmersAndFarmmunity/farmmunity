package com.shop.farmmunity.domain.member.service;

import com.shop.farmmunity.domain.member.dto.AddressFormDto;
import com.shop.farmmunity.domain.member.entity.Address;
import com.shop.farmmunity.domain.member.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }
}
