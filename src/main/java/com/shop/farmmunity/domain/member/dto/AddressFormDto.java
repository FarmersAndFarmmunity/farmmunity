package com.shop.farmmunity.domain.member.dto;

import com.shop.farmmunity.domain.member.entity.Address;
import com.shop.farmmunity.domain.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class AddressFormDto {

    @NotBlank(message = "배송지명은 필수 입력 값입니다.")
    private String shippingName;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String recipientName;

    @NotBlank(message = "연락처는 필수 입력 값입니다.")
    private String recipientContact;

    @NotBlank(message = "우편번호는 필수 입력 값입니다.")
    private String zipcode;

    @NotBlank(message = "주소는 필수 입력 값입니다.")
    private String addr;

    @NotNull
    private String addr_detail;

    private Boolean is_default; // 기본 배송지 등록 여부

    private Member member;

    private static ModelMapper modelMapper = new ModelMapper();

    public Address createAddressForm(Member member) {
        this.member = member;
        return modelMapper.map(this, Address.class);
    }

}
