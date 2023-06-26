package com.shop.farmmunity.domain.review.dto;

import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.review.entity.Review;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ReviewFormDto {

    private Long id;

    private Member member;

    private Item item;

    private Long itemId; // 아이템 id

    @Length(min = 20, max = 200, message = "리뷰는 최소 20자 이상 입력하셔야 합니다.")
    @NotBlank(message = "리뷰 내용은 필수 입력 값입니다.")
    private String contents;

    private static ModelMapper modelMapper = new ModelMapper();

    public Review createReview(Member member) {
        this.member = member;
        return modelMapper.map(this, Review.class);
    }

}
