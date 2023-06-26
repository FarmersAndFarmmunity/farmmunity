package com.shop.farmmunity.domain.review.repository;

import com.shop.farmmunity.domain.review.dto.ReviewSearchDto;
import com.shop.farmmunity.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {
    Page<Review> getMyReviewPage(ReviewSearchDto reviewSearchDto, Pageable pageable, Long id);
}
