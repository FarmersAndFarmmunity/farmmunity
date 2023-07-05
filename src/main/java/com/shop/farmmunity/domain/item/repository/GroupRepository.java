package com.shop.farmmunity.domain.item.repository;


import com.shop.farmmunity.domain.item.constant.GroupBuyStatus;
import com.shop.farmmunity.domain.item.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Group findByItemIdAndStatus(long itemId, GroupBuyStatus status);
    Group findByMemberIdAndStatus(long itemId, GroupBuyStatus status);
    Group findByOrderId(long orderId);
    Long countByItemIdAndStatus(long itemId, GroupBuyStatus status);
}
