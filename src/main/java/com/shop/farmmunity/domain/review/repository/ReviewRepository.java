package com.shop.farmmunity.domain.review.repository;

import com.shop.farmmunity.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {
    List<Review> findByItemIdLike(Long itemId);
}
