package com.shop.farmmunity.domain.order.dto;

import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.order.constant.Customer;
import com.shop.farmmunity.domain.order.constant.Recipient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class OrderCplDto {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String customerName;

    @NotBlank(message = "연락처는 필수 입력 값입니다.")
    private String contact;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String customerEmail;

    @NotNull
    private String comment; // 배송 요청 사항

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

    private static ModelMapper modelMapper = new ModelMapper();

    public Customer createCustomer() {
        return modelMapper.map(this, Customer.class);
    }

    public Recipient createRecipient() {
        return modelMapper.map(this, Recipient.class);
    }
}