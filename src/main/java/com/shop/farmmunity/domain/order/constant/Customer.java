package com.shop.farmmunity.domain.order.constant;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter
@Getter
public class Customer {

    private String customerName;

    private String contact;

    private String customerEmail;

    private String comment;
}
