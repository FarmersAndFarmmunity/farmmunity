package com.shop.farmmunity.domain.payment.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.shop.farmmunity.domain.order.entity.Order;
import com.shop.farmmunity.domain.payment.constant.PaymentDtlDto;
import com.shop.farmmunity.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{id}/success") // 결제 성공
    public String confirmPayment(
            @PathVariable Long id, // 숫자형 주문 번호
            String paymentKey, // 결제 고유키
            String orderId, // UUID가 붙은 주문 번호
            Long amount, // 결제 금액
            Model model
    ) throws Exception {

        Long parsedOrderId = Long.parseLong(orderId.split("__")[1]);

        Order order = paymentService.verifyRequest(id, parsedOrderId, amount); // 클라이언트로부터 받아온 정보 검증
        ResponseEntity<JsonNode> responseEntity = paymentService.requestPayment(paymentKey, orderId, amount);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            JsonNode jsonNode = responseEntity.getBody();
            paymentService.payDone(order, jsonNode);
            return "redirect:/order/%d?msg=%s".formatted(order.getId(), URLEncoder.encode("결제가 완료되었습니다.", StandardCharsets.UTF_8));
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());

            return "order/orderFail";
        }
    }

    @GetMapping("/{id}/fail") // 결제 실패
    public String failPayment(String message, String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "order/orderFail";
    }

    @GetMapping("/payment/{orderId}") // 결제내역
    public String paymentDtl(@PathVariable("orderId") Long orderId, Principal principal, Model model) {
        paymentService.validatePayment(orderId, principal.getName());
        PaymentDtlDto payment = paymentService.getPayment(orderId);
        model.addAttribute("payment", payment);
        return "payment/paymentDtl";
    }
}
