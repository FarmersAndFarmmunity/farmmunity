package com.shop.farmmunity.domain.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewSearchDto {
    private String searchDateType;
    private String searchBy;
    private String searchQuery = "";
}