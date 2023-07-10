package com.shop.farmmunity.domain.order.entity;

import com.shop.farmmunity.base.baseEntity.BaseEntity;
import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.item.entity.ItemOption;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Getter
@Setter
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;  // 주문가격

    private int count;  // 수량

    private String optionNm; // 옵션 이름

    private int optionQuantity; // 옵션 수량

    public static OrderItem createOrderItem(Item item, int count, Long itemOptionId) {

        String fullOptionNm = null;
        int optQuntity = 1;
        int price = item.getPrice();

        if (itemOptionId != null) {
            ItemOption itemOption = item.getItemOptionList().stream().filter(i -> Objects.equals(i.getId(), itemOptionId)).findAny().orElseThrow(EntityNotFoundException::new);
            price = itemOption.getExtraAmount();
            fullOptionNm = itemOption.getOptionName() + " : " + itemOption.getQuantity() + " (" + itemOption.getExtraAmount() + "원)";
            optQuntity = itemOption.getQuantity();
        }
        item.checkRestStock(count * optQuntity); // 주문 전에 상품 재고 체크부터

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item); // 주문한 아이템을 지정
        orderItem.setCount(count); // 주문한 아이템을 몇 개를 살지
        orderItem.setOrderPrice(price); // 해당 상품의 가격을 저장
        orderItem.setOptionNm(fullOptionNm);
        orderItem.setOptionQuantity(optQuntity);

        return orderItem;
    }

    public int getTotalPrice() {
        return orderPrice * count;
    }

    public void cancel() {
        this.getItem().addStock(count * optionQuantity);
    }

    public void payDone() {
        // 원래는 createOrderItem 메소드 36줄에 removeStock이 있었는데
        // 주문 후 결제까지 완료 되어서야 상품 재고 빠지도록 설계
        // 기존 코드는 주문만 하고 결제를 안해도 재고가 빠지는 문제점이 있었음
        this.getItem().removeStock(count * optionQuantity);
    }
}