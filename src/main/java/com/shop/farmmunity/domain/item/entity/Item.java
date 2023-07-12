package com.shop.farmmunity.domain.item.entity;

import com.shop.farmmunity.base.baseEntity.BaseEntity;
import com.shop.farmmunity.base.exception.OutOfStockException;
import com.shop.farmmunity.domain.item.constant.ItemClassifyStatus;
import com.shop.farmmunity.domain.item.constant.ItemSellStatus;
import com.shop.farmmunity.domain.item.dto.ItemFormDto;
import com.shop.farmmunity.domain.itemTag.entity.ItemTag;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 코드

    @Column(nullable = false, length = 50)
    private String itemNm; // 상품명

    @Column(name = "price", nullable = false)
    private int price; // 가격

    @Column(nullable = false)
    private int stockNumber; // 재고 수량

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String itemDetail; // 상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; // 상품 판매 상태

    @Enumerated(EnumType.STRING)
    private ItemClassifyStatus itemClassifyStatus; // 상품 카테고리

    @Column(nullable = false,  length = 50)
    private String itemTagContents;

    @Builder.Default
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.EXTRA)
    Set<ItemTag> itemTags = new LinkedHashSet<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOption> itemOptionList = new ArrayList<>(); // 상품 옵션
  
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_buying_id")
    private GroupBuying groupBuying;

    public void updateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        this.itemTagContents = itemFormDto.getItemTagContents();
        this.groupBuying.setDiscount(itemFormDto.getDiscount());
    }

    // 재고 수량을 계산 (주문이 들어올 때에 주문 수량만큼 재고 수량을 빼줌)
    public void removeStock(int stockNumber) {

        checkRestStock(stockNumber);
        this.stockNumber -= stockNumber;
    }

    public void checkRestStock(int stockNumber) {
        int restStock = this.stockNumber - stockNumber;
        if (restStock < 0) {
            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: " + this.stockNumber + ")");
        }
    }

    // 주문 취소 시 주문 수량 만큼 상품 재고를 증가
    public void addStock(int stockNumber) {
        this.stockNumber += stockNumber;
    }

    public void updateItemTags(Set<ItemTag> newItemTags) {
        Set<ItemTag> needToDelete = itemTags
                .stream()
                .filter(Predicate.not(newItemTags::contains))
                .collect(Collectors.toSet());

        needToDelete
                .stream()
                .forEach(itemTags::remove);

        newItemTags
                .stream()
                .forEach(itemTags::add);
    }
}