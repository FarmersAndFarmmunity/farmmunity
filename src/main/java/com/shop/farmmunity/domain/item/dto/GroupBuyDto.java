package com.shop.farmmunity.domain.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GroupBuyDto {
    private long count;
    private String username;
}
