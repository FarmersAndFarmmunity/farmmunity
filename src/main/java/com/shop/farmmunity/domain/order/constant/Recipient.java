package com.shop.farmmunity.domain.order.constant;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter
@Getter
public class Recipient {

    private String shippingName;

    private String recipientName;

    private String recipientContact;

    private String zipcode;

    private String addr;

    private String addr_detail;
}