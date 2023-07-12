package com.shop.farmmunity.domain.member.dto;

import com.shop.farmmunity.domain.item.dto.ItemFormDto;
import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateRequestDto {
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String username;

    private String currentPassword;

    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "새 비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
    private String newPassword;

    private String confirmPassword;
}
