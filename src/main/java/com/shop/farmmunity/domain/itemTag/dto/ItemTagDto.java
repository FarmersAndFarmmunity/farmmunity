package com.shop.farmmunity.domain.itemTag.dto;

import com.shop.farmmunity.domain.item.dto.ItemFormDto;
import com.shop.farmmunity.domain.item.entity.Item;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ItemTagDto {

    private Long id;

    private String itemNm;

    private String itemDetail;

    private String imgUrl;

    private int price;

    private String itemTagContents;

    private static ModelMapper modelMapper = new ModelMapper();

    public static ItemTagDto of(Item item) {
        return modelMapper.map(item,ItemTagDto.class);
    }
}
