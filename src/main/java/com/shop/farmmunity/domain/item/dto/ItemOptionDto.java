package com.shop.farmmunity.domain.item.dto;

import com.shop.farmmunity.domain.item.entity.ItemOption;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter @Setter
public class ItemOptionDto {

    private Long id;
    private String optionName; // 옵션 이름
    private int extraAmount; // 추가 가격
    private int quantity; // 옵션 수량
    private int gbPrice; // 공동구매 가격
    private static ModelMapper modelMapper = new ModelMapper();

    public static ItemOptionDto of(ItemOption itemOption) {
        return modelMapper.map(itemOption, ItemOptionDto.class); //ItemImg 객체의 자료형과 멤버변수의 이름이 같을 때 ItemImgDto로 값 복사
    }
}
