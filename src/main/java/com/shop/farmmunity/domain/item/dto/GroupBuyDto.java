package com.shop.farmmunity.domain.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupBuyDto {

    private long count; // 판매량
    private String username; // 공동구매 호스트 아이디
    private String partnerUsername; // 공동구매 참여자 아이디
    private String MatchedTime; // 매칭 성공 시간
    private String MatchEndTime; // 매칭 종료 시간
}
