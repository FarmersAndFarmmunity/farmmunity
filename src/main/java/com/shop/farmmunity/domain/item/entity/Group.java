package com.shop.farmmunity.domain.item.entity;

import com.shop.farmmunity.base.baseEntity.BaseEntity;
import com.shop.farmmunity.domain.item.constant.GroupBuyStatus;
import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.order.constant.OrderStatus;
import com.shop.farmmunity.domain.order.entity.Order;
import com.shop.farmmunity.domain.order.entity.OrderItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;


@Entity
@Table(name = "group_buy_member")
@Getter
@Setter
@ToString
public class Group extends BaseEntity {

    @Id
    @Column(name = "group_buy_member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isHost; // 공동 구매 주최 여부(1인 상태 여부), 주최 단위로 구매 진행

    private GroupBuyStatus status; // 공동 구매 매칭 성공 여부

    private long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @JoinColumn(name = "partner_member_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Member partnerMember; // 매칭 파트너의 멤버 아이디, null일 경우 매칭되지 않은 상태

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="order_id")
    private Order order; // 공동구매하는 주문

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime groupBuyStartTime; // 모집 시작 시간

    private LocalDateTime groupBuyMatchedTime; // 매칭된 시각

    @Column(updatable = false)
    private LocalDateTime groupBuyEndTime; // 모집 마감 시간

    public static Group createHostGroup(Member member, Order order) {
        Group group = new Group();
        group.member = member;
        group.order = order;
        for(OrderItem orderItem : order.getOrderItems()){
            group.itemId = orderItem.getItem().getId(); // 현재는 장바구니 기능 없이 한번에 하나의 상품만 주문하므로 List가 아니라 변수로 처리
        }
        group.setGroupBuyStartTime(LocalDateTime.now());
        group.setGroupBuyEndTime(group.getGroupBuyStartTime().plusHours(24)); // 공동구매 모집 마감 시간은 24시간
        group.isHost = true; // 공동구매를 시작하면 우선 호스트로 지정함
        group.status = GroupBuyStatus.FAIL;
        return group;
    }

    public void updateClientGroup(Member partnerMember) {
        setPartner(partnerMember); // 타인에게 매칭된 경우 파트너 멤버를 세팅
        isHost = false; // 호스트 여부를 거짓으로 선언
    }

    public void setPartner(Member partnerMember){
        this.groupBuyMatchedTime = LocalDateTime.now();
        this.status = GroupBuyStatus.SUCCESS;
        this.partnerMember = partnerMember;
    }
}
