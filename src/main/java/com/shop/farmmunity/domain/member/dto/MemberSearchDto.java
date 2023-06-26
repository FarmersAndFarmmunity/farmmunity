package com.shop.farmmunity.domain.member.dto;


import com.shop.farmmunity.domain.member.constant.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSearchDto {
    private String searchDateType; // 계정 생성 기간 검색용 변수

    private Role searchRole; // 계정 권한 검색용 변수

    private String searchBy; // 계정을 어떤 카테고리에서 검색할지 지정하는 변수

    private String searchQuery = ""; // 검색어 변수
}
