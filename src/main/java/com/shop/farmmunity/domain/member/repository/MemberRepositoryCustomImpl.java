package com.shop.farmmunity.domain.member.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.farmmunity.domain.member.constant.Role;
import com.shop.farmmunity.domain.member.dto.MemberSearchDto;
import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.member.entity.QMember;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    public MemberRepositoryCustomImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchRoleEq(Role searchRole) {
        return searchRole == null ? null : QMember.member.role.eq(searchRole);
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

        return QMember.member.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {

        if (StringUtils.equals("name", searchBy)) {
            return QMember.member.username.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("email", searchBy)) {
            return QMember.member.email.like("%" + searchQuery + "%");
        }
        return null;
    }

    @Override
    public Page<Member> getAdminMemberPage(MemberSearchDto memberSearchDto, Pageable pageable) {

        List<Member> content = queryFactory
                .selectFrom(QMember.member)
                .where(regDtsAfter(memberSearchDto.getSearchDateType()),
                        searchRoleEq(memberSearchDto.getSearchRole()),
                        searchByLike(memberSearchDto.getSearchBy(),
                                memberSearchDto.getSearchQuery()))
                .orderBy(QMember.member.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(Wildcard.count).from(QMember.member)
                .where(regDtsAfter(memberSearchDto.getSearchDateType()),
                        searchRoleEq(memberSearchDto.getSearchRole()),
                        searchByLike(memberSearchDto.getSearchBy(), memberSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }


}