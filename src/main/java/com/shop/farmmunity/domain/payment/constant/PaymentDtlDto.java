package com.shop.farmmunity.domain.payment.constant;

import com.shop.farmmunity.domain.payment.entity.Payment;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentDtlDto {

    private String payType;

    private Long amount; // 결제 금액

    private Long orderId;                    // 주문고유번호 uuid

    private String orderName; // 주문 이름

    private String customerEmail;            // 주문자 이메일

    private String customerName;            // 주문자 성함

    private String paymentKey;                // 결제 고유 번호

    private LocalDateTime regTime; // 거래 일시

    private static ModelMapper modelMapper = new ModelMapper();

    public static PaymentDtlDto of(Payment payment) {
        return modelMapper.map(payment, PaymentDtlDto.class);
    }
}
