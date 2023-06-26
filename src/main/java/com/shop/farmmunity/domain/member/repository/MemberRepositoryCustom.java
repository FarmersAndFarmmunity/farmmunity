package com.shop.farmmunity.domain.member.repository;

import com.shop.farmmunity.domain.member.dto.MemberSearchDto;
import com.shop.farmmunity.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<Member> getAdminMemberPage(MemberSearchDto memberSearchDto, Pageable pageable);
}
