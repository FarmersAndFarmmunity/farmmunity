package com.shop.farmmunity.domain.member.service;

import com.shop.farmmunity.domain.member.dto.AddressDto;
import com.shop.farmmunity.domain.member.dto.AddressFormDto;
import com.shop.farmmunity.domain.member.entity.Address;
import com.shop.farmmunity.domain.member.repository.AddressRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

    public Long updateAddress(AddressFormDto addressFormDto) {
        Address address = findAddressById(addressFormDto.getAddressId());
        address.updateAddress(addressFormDto);
        return address.getId();
    }

    @Transactional(readOnly = true)
    public List<AddressDto> getAddressList(Long memberId) {
        return addressRepository.findAddressList(memberId);
    }

    @Transactional(readOnly = true)
    public AddressDto getAddress(Long memberId) {
        Address address = findAddressById(memberId);
        return address.toDto();
    }

    @Transactional(readOnly = true)
    public Address findAddressById(Long addressId) {
        return addressRepository.findById(addressId).orElseThrow(EntityNotFoundException::new);
    }
}
