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
    @JoinColumn(name = "item_option_id")
    private ItemOption itemOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;  // 주문가격

    private int count;  // 수량

    private String optionNm; // 옵션 이름

    private boolean isGroupBuying; // 개별 물품의 공동구매 여부(아직 미사용)
  
    // 일반 구매 orderItem 생성
    public static OrderItem createOrderItem(Item item, int count, Long itemOptionId) {

        ItemOption itemOption = null;
        String fullOptionNm = null;
        int price = item.getPrice();

        if (itemOptionId != null) {
            itemOption = item.getItemOptionList().stream().filter(i -> Objects.equals(i.getId(), itemOptionId)).findAny().orElseThrow(EntityNotFoundException::new);
            price = itemOption.getExtraAmount();
            fullOptionNm = itemOption.getOptionName();
        }
        item.checkRestStock(count); // 주문 전에 상품 재고 체크부터

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item); // 주문한 아이템을 지정
        orderItem.setItemOption(itemOption); // 주문한 옵션을 지정 (옵션이 없다면 null로 지정)
        orderItem.setCount(count); // 주문한 아이템을 몇 개를 살지
        orderItem.setOrderPrice(price); // 해당 상품의 가격을 저장
        orderItem.setOptionNm(fullOptionNm);

        return orderItem;
    }

    // 공동 구매 orderItem 생성
    public static OrderItem createGroupBuyingOrderItem(Item item, int count, Long itemOptionId) {

        ItemOption itemOption = null;
        String fullOptionNm = null;
        int price = item.getGroupBuying().getDiscount();

        if (itemOptionId != null) {
            itemOption = item.getItemOptionList().stream().filter(i -> Objects.equals(i.getId(), itemOptionId)).findAny().orElseThrow(EntityNotFoundException::new);
            price = itemOption.getGbPrice();
            fullOptionNm = itemOption.getOptionName();
        }

        item.checkRestStock(count); // 주문 전에 상품 재고 체크부터
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item); // 주문한 아이템을 지정
        orderItem.setItemOption(itemOption); // 주문한 옵션을 지정 (옵션이 없다면 null로 지정)
        orderItem.setCount(count); // 주문한 아이템을 몇 개를 살지
        orderItem.setOrderPrice(price); // 해당 상품의 가격을 저장
        orderItem.setOptionNm(fullOptionNm);

        return orderItem;
    }

    public int getTotalPrice() {
        return orderPrice * count;
    }

    public void cancel() {
        if (itemOption != null) {
            itemOption.addStock(count);
        } else {
            this.getItem().addStock(count);
        }
    }

    public void payDone() {
        // 원래는 createOrderItem 메소드 36줄에 removeStock이 있었는데
        // 주문 후 결제까지 완료 되어서야 상품 재고 빠지도록 설계
        // 기존 코드는 주문만 하고 결제를 안해도 재고가 빠지는 문제점이 있었음
        if (itemOption != null) {
            itemOption.removeStock(count);
        } else {
            this.getItem().removeStock(count);
        }
    }
}