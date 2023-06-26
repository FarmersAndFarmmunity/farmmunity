package com.shop.farmmunity.domain.member.repository;

import com.shop.farmmunity.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Member findByEmail(String email);

    Optional<Member> findByUsername(String username);

    Optional<Member> findById(Long id);
}
