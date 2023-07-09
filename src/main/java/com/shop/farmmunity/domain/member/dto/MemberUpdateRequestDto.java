package com.shop.farmmunity.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateRequestDto {
    private String username;
    private String email;
    private String password;
    private String address;
    private LocalDateTime latestUpdateTime;
}
