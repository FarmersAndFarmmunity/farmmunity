package com.shop.farmmunity.domain.member.repository;

import com.shop.farmmunity.domain.member.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long>, AddressRepositoryCustom {

}
