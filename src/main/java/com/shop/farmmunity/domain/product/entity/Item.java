package com.shop.farmmunity.domain.product.entity;

import com.shop.farmmunity.domain.product.constant.ItemClassifyStatus;
import com.shop.farmmunity.domain.product.constant.ItemSellStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Column(name="price", nullable = false)
    private int price; //가격

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    @Column(columnDefinition = "TEXT")
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    @Enumerated(EnumType.STRING)
    private ItemClassifyStatus itemClassifyStatus; //상품 카테고리

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();

//    public void updateItem(ItemFormDto itemFormDto){
//        this.itemNm = itemFormDto.getItemNm();
//        this.price = itemFormDto.getPrice();
//        this.stockNumber = itemFormDto.getStockNumber();
//        this.itemDetail = itemFormDto.getItemDetail();
//        this.itemSellStatus = itemFormDto.getItemSellStatus();
//    }

    // 재고 수량을 계산 (주문이 들어올 때에 주문 수량만큼 재고 수량을 빼줌)
//    public void removeStock(int stockNumber) {
//
//        checkRestStock(stockNumber);
//        this.stockNumber -= stockNumber;
//    }

//    public void checkRestStock(int stockNumber) {
//        int restStock = this.stockNumber - stockNumber;
//        if (restStock < 0) {
//            throw new OutOfStockException("상품의 재고가 부족 합니다. (현재 재고 수량: " + this.stockNumber + ")");
//        }
//    }

    // 주문 취소 시 주문 수량 만큼 상품 재고를 증가
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }

}
