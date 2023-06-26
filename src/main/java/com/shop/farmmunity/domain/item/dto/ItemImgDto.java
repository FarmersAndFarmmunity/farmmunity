package com.shop.farmmunity.domain.item.dto;

import com.shop.farmmunity.domain.item.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ItemImgDto {

    private Long id;
    private String imgName;
    private String oriImgName;
    private String imgUrl;
    private String repImgYn;
    private static ModelMapper modelMapper = new ModelMapper();

    public static ItemImgDto of(ItemImg itemImg) {
        return modelMapper.map(itemImg, ItemImgDto.class); //ItemImg 객체의 자료형과 멤버변수의 이름이 같을 때 ItemImgDto로 값 복사
    }
}
