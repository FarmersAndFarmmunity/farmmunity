package com.shop.farmmunity.domain.review.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.farmmunity.domain.item.entity.QItem;
import com.shop.farmmunity.domain.review.dto.ReviewSearchDto;
import com.shop.farmmunity.domain.review.entity.QReview;
import com.shop.farmmunity.domain.review.entity.Review;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ReviewRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression regDtsAfter(String searchDateType) {

        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {
            return null;
        } else if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if (StringUtils.equals("contents", searchBy)) {
            return QReview.review.contents.like("%" + searchQuery + "%");
        }

        return null;
    }


    @Override
    public Page<Review> getMyReviewPage(ReviewSearchDto reviewSearchDto, Pageable pageable, Long id) {
        List<Review> content = queryFactory
                .selectFrom(QReview.review)
                .where(QReview.review.member.id.eq(id),
                        regDtsAfter(reviewSearchDto.getSearchDateType()),
                        searchByLike(reviewSearchDto.getSearchBy(),
                                reviewSearchDto.getSearchQuery()))
                .orderBy(QReview.review.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(Wildcard.count).from(QReview.review)
                .where(QReview.review.member.id.eq(id),
                        regDtsAfter(reviewSearchDto.getSearchDateType()),
                        searchByLike(reviewSearchDto.getSearchBy(),
                                reviewSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
