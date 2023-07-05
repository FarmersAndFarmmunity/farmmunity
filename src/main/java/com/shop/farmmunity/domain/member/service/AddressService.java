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

    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }

    public void modifyDefaultAddress(Long memberId) {

        Address defaultAddress = findDefaultAddress(memberId);
        if (defaultAddress != null) {
            defaultAddress.setIs_default(false);
        }
    }

    public Address findDefaultAddress(Long memberId) {
        return addressRepository.findDefaultAddress(memberId);
    }

    @Transactional(readOnly = true)
    public List<AddressDto> getAddressList(Long memberId) {
        return addressRepository.findAddressList(memberId);
    }

    @Transactional(readOnly = true)
    public AddressDto getAddress(Long addressId) {
        Address address = findAddressById(addressId);
        return address.toDto();
    }

    @Transactional(readOnly = true)
    public Address findAddressById(Long addressId) {
        return addressRepository.findById(addressId).orElseThrow(EntityNotFoundException::new);
    }
}
