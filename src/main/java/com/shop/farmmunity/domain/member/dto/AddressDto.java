package com.shop.farmmunity.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddressDto {

    private Long addressId;

    private String shippingName;

    private String recipientName;

    private String recipientContact;

    private String zipcode;

    private String addr;

    private String addr_detail;

    private Boolean is_default;

    public AddressDto() {
        this.shippingName = "";
        this.recipientName = "";
        this.recipientContact = "";
        this.zipcode = "";
        this.addr = "";
        this.addr_detail = "";
    }
}
