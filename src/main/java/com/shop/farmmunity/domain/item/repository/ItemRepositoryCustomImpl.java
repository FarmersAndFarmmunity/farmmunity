package com.shop.farmmunity.domain.item.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.farmmunity.domain.item.constant.ItemClassifyStatus;
import com.shop.farmmunity.domain.item.constant.ItemSellStatus;
import com.shop.farmmunity.domain.item.dto.ItemClassifyDto;
import com.shop.farmmunity.domain.item.dto.ItemSearchDto;
import com.shop.farmmunity.domain.item.dto.MainItemDto;
import com.shop.farmmunity.domain.item.dto.QMainItemDto;
import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.item.entity.QItem;
import com.shop.farmmunity.domain.item.entity.QItemImg;
import com.shop.farmmunity.domain.itemKeyword.entity.ItemKeyword;
import com.shop.farmmunity.domain.itemTag.entity.QItemTag;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
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

        if (StringUtils.equals("itemNm", searchBy)) {
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if (StringUtils.equals("createdBy", searchBy)) {
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        List<Item> content = jpaQueryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Item> getMyItemPage(ItemSearchDto itemSearchDto, Pageable pageable, String email) {
        List<Item> content = jpaQueryFactory
                .selectFrom(QItem.item)
                .where(QItem.item.createdBy.eq(email),
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression itemNmLike(String searchQuery) {
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }

    private BooleanExpression classifyItemStatusEq(ItemClassifyStatus itemClassifyStatus) {
        if (itemClassifyStatus == ItemClassifyStatus.AGRICULTURE) {
            return QItem.item.itemClassifyStatus.eq(ItemClassifyStatus.AGRICULTURE);
        } else if (itemClassifyStatus == ItemClassifyStatus.MARINE) {
            return QItem.item.itemClassifyStatus.eq(ItemClassifyStatus.MARINE);
        } else if (itemClassifyStatus == ItemClassifyStatus.LIVESTOCK) {
            return QItem.item.itemClassifyStatus.eq(ItemClassifyStatus.LIVESTOCK);
        }
        return null;
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemClassifyDto itemClassifyDto, ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> content = jpaQueryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price,
                                item.itemClassifyStatus)
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(classifyItemStatusEq(itemClassifyDto.getItemClassifyStatus()))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = jpaQueryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(classifyItemStatusEq(itemClassifyDto.getItemClassifyStatus()))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}