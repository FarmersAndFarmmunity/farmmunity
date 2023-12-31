package com.shop.farmmunity.domain.order.service;

import com.shop.farmmunity.domain.item.constant.GroupBuyStatus;
import com.shop.farmmunity.domain.item.entity.Group;
import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.item.entity.ItemImg;
import com.shop.farmmunity.domain.item.repository.GroupRepository;
import com.shop.farmmunity.domain.item.repository.ItemImgRepository;
import com.shop.farmmunity.domain.item.repository.ItemOptionRepository;
import com.shop.farmmunity.domain.item.repository.ItemRepository;
import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.member.repository.MemberRepository;
import com.shop.farmmunity.domain.order.dto.*;
import com.shop.farmmunity.domain.order.entity.Delivery;
import com.shop.farmmunity.domain.order.entity.Order;
import com.shop.farmmunity.domain.order.entity.OrderItem;
import com.shop.farmmunity.domain.order.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
@Transactional
@RequiredArgsConstructor
@EnableScheduling
public class OrderService {

    private final ItemRepository itemRepository;
    private final ItemOptionRepository itemOptionRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;
    private final GroupRepository groupRepository;

    @Value("${custom.toss_client}")
    private String CLIENT_KEY;
    @Value("${custom.site.baseUrl}")
    private String baseUrl;

    // 일반 주문
    public Long order(OrderDto orderDto, String email) {
        Item item = itemRepository.findById(orderDto.getItemId()) // 주문할 상품 조회
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email); // 현재 로그인한 회원의 이메일로 회원 정보 조회

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount(), orderDto.getItemOptionId()); // 주문할 상품 엔티티와 주문 수량을 이용하여 주문 상품 엔티티 생성
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList, false); // 회원 정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티를 생성
        orderRepository.save(order); // 생성한 주문 엔티티를 저장

        return order.getId();
    }

    // 공동구매 주문
    public Long groupOrder(OrderDto orderDto, String email) {
        Item item = itemRepository.findById(orderDto.getItemId()) // 주문할 상품 조회
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email); // 현재 로그인한 회원의 이메일로 회원 정보 조회

        List<OrderItem> orderItemList = new ArrayList<>();
//        OrderItem orderItem = OrderItem.createGroupBuyingOrderItem(item, item.getGroupBuying().getDiscount(), orderDto.getCount()); // 주문할 상품 엔티티와 주문 수량을 이용하여 주문 상품 엔티티 생성
        OrderItem orderItem = OrderItem.createGroupBuyingOrderItem(item, orderDto.getCount(), orderDto.getItemOptionId()); // 주문할 상품 엔티티와 주문 수량을 이용하여 주문 상품 엔티티 생성
        orderItemList.add(orderItem);

        if(groupRepository.findByMemberIdAndItemIdAndStatus(member.getId(), item.getId(), GroupBuyStatus.WAIT) != null) return -1L; // 이미 대기열에 있는 본인의 공동구매 주문건이 있을경우 주문을 취소시킴

        Order order = Order.createOrder(member, orderItemList, true); // 회원 정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티를 생성
        orderRepository.save(order); // 생성한 주문 엔티티를 저장
        return order.getId();
    }

    public void orderComplete(Long orderId, OrderCplDto orderCplDto) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Delivery delivery = orderCplDto.createDelivery();
        order.setDelivery(delivery);
    }

    @Transactional(readOnly = true)
    public OrderDtlDto getOrderDtl(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);

        List<OrderItem> orderItems = order.getOrderItems();

        OrderDtlDto orderDtlDto = new OrderDtlDto(order, CLIENT_KEY, baseUrl);

        for (OrderItem orderItem : orderItems) {
            ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn
                    (orderItem.getItem().getId(), "Y");
            OrderItemDtlDto orderItemDtlDto =
                    new OrderItemDtlDto(orderItem, itemImg.getImgUrl());
            orderDtlDto.addOrderItemDto(orderItemDtlDto);
        }

        return orderDtlDto;
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn
                        (orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto =
                        new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            if(order.isGroupBuying()) {
                Group group = groupRepository.findByOrderId(order.getId());
                orderHistDto.setGroupBuyStatus(group.getStatus());
                orderHistDto.setMatchEndTime(group.getGroupBuyEndTime().toString().substring(0,19).replace("T", " "));
                if(group.getStatus() == GroupBuyStatus.SUCCESS) orderHistDto.updatePartnerUsername(group.getPartnerMember().getUsername());
                System.out.println(orderHistDto.getMatchEndTime());
                System.out.println(orderHistDto.getPartnerUsername());
            }
            orderHistDtos.add(orderHistDto);
        }

        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) { // 현재 로그인한 사용자와 주문한 사용자가 같은지 검사
        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        return StringUtils.equals(curMember.getEmail(), savedMember.getEmail());
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder(); // 주문 취소 하면 변경 감지 기능에 의해서 트랜잭션이 끝날 때 update 쿼리가 실행됨
        if(order.isGroupBuying()) {
            // 공동구매가 진행중이었다면 상태값을 실패로 변경
            groupRepository.findByOrderId(orderId).setStatus(GroupBuyStatus.FAIL);
        }
    }

    // 공동구매 자동 취소 로직
    @Scheduled(cron = "0 0 * * * *")
    public void autoCancelOrder() {
        // 매 시 정각마다 현재 대기중 상태이나 공동구매 대기 마감 시간이 지난 것들을 취소시킴
        List<Group> failGroupOrder = groupRepository.findByStatusAndGroupBuyEndTimeBefore(GroupBuyStatus.WAIT, LocalDateTime.now());
        for(Group fail : failGroupOrder){
            fail.setStatus(GroupBuyStatus.FAIL);
            Optional<Order> order = orderRepository.findById(fail.getOrder().getId());
            order.get().cancelOrder();
        }
        log.info("자동 주문 취소가 실행되었습니다.");
    }

    // 장바구니의 상품 주문 메서드
    public Long orders(List<OrderDto> orderDtoList, String email) { // 구매자가 가진 주문 갯수를 가져옴
        Member member = memberRepository.findByEmail(email); // 현재 구매자의 정보를 가져옴

        List<OrderItem> orderItemList = new ArrayList<>(); // 주문 상품의 정보를 담을 리스트

        // 주문 상품에 대한 정보(상품 아이디, 해당 상품의 주문 갯수)에 대해 주문할 상품 리스트를 만들어 줌
        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new); // 해당 상품에 대한 갯수 정보

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount(), orderDto.getItemOptionId()); // 주문한 제품에 대한 정보와 수량 정보를 저장, 재고 계산 후 주문 정보를 담은 객체를 반환

            orderItemList.add(orderItem);
        }

        // 최종 주문을 생성
        // 현재는 공동구매 장바구니 기능을 지원하지 않았으므로 공동구매 여부가 무조건 false
        Order order = Order.createOrder(member, orderItemList, false);

        // 주문 데이터를 저장
        orderRepository.save(order);

        return order.getId(); // 해당 주문이 생성되면 결과 값으로 주문에 대한 아이디 값을 반환
    }

    public Optional<Order> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }
}