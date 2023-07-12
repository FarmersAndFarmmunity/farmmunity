package com.shop.farmmunity.domain.member.entity;

import com.shop.farmmunity.domain.member.dto.AddressDto;
import com.shop.farmmunity.domain.member.dto.AddressFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "address")
@Getter
@Setter
@ToString
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String shippingName;

    private String recipientName;

    private String recipientContact;

    private String zipcode;

    private String addr;

    private String addr_detail;

    private Boolean is_default; // 기본 배송지 등록 여부

    public void updateAddress(AddressFormDto addressFormDto) {
        this.shippingName = addressFormDto.getShippingName();
        this.recipientName = addressFormDto.getRecipientName();
        this.recipientContact = addressFormDto.getRecipientContact();
        this.zipcode = addressFormDto.getZipcode();
        this.addr = addressFormDto.getAddr();
        this.addr_detail = addressFormDto.getAddr_detail();
        this.is_default = addressFormDto.getIs_default();
    }

    public AddressDto toDto() {
        AddressDto addressDto = new AddressDto();
        addressDto.setAddressId(this.id);
        addressDto.setZipcode(this.zipcode);
        addressDto.setAddr_detail(this.addr_detail);
        addressDto.setAddr(this.addr);
        addressDto.setIs_default(this.is_default);
        addressDto.setRecipientContact(this.recipientContact);
        addressDto.setRecipientName(this.recipientName);
        addressDto.setShippingName(this.shippingName);
        return addressDto;
    }
}
