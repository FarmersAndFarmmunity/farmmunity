package com.shop.farmmunity.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDto {

    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long itemId;

    @Positive(message = "옵션을 선택해주세요.")
    private Long itemOptionId;

    @Min(value = 1, message = "최소 1개 이상 담아주세요.")
    private int count;
}
