package com.shop.farmmunity.domain.order.repository;

import com.shop.farmmunity.domain.order.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o " +
            "where o.member.email = :email " +
            "and o.isPaid = true " +
            "order by o.orderDate desc"
    )
        // o.isPaid = true 조건식을 추가하여 결제 완료 된 주문만 불러오도록 함
    List<Order> findOrders(@Param("email") String email, Pageable pageable);

    @Query("select count(o) from Order o " +
            "where o.member.email = :email"
    )
    Long countOrder(@Param("email") String email);

}