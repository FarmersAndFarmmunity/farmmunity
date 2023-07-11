package com.shop.farmmunity.domain.cart.dto;

import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.item.entity.ItemOption;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.text.NumberFormat;
import java.util.Objects;

@Getter
@Setter
public class CartDetailDto {
    private Long cartItemId; // 카트 아이템 아이디
    private String itemNm; // 상품명
    private int price; // 상품금액
    private int count; // 상품 수량
    private String imgUrl; // 상품 이미지 경로
    private String optionNm; // 옵션 이름

    public CartDetailDto(Long cartItemId, String itemNm, Long itemOptionId, int price, int count, String imgUrl, Item item) {

        this.cartItemId = cartItemId;
        this.itemNm = itemNm;
        this.price = price;
        this.count = count;
        this.imgUrl = imgUrl;
        this.optionNm = null;
        if (itemOptionId != null) {
            ItemOption itemOption = item.getItemOptionList().stream().filter(i -> Objects.equals(i.getId(), itemOptionId)).findAny().orElseThrow(EntityNotFoundException::new);
            this.price = itemOption.getExtraAmount();
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            String formattedPrice = numberFormat.format(itemOption.getExtraAmount());
            this.optionNm = itemOption.getOptionName() + " " + itemOption.getQuantity() + " (" + formattedPrice + ")";
        }
    }
}
