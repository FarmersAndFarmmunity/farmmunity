package com.shop.farmmunity.domain.item.repository;

import com.shop.farmmunity.domain.item.entity.GroupBuying;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupBuyingRepository extends JpaRepository<GroupBuying, Long>{
    void deleteById(long groupBuyingId);

    GroupBuying findByItemId(long itemId);

}
