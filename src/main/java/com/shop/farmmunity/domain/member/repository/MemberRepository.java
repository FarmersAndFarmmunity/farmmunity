package com.shop.farmmunity.domain.member.repository;

import com.shop.farmmunity.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
