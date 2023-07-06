package com.shop.farmmunity.domain.order.controller;

import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.member.service.MemberService;
import com.shop.farmmunity.domain.order.dto.OrderCplDto;
import com.shop.farmmunity.domain.order.dto.OrderDtlDto;
import com.shop.farmmunity.domain.order.dto.OrderDto;
import com.shop.farmmunity.domain.order.dto.OrderHistDto;
import com.shop.farmmunity.domain.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final MemberService memberService;

    @GetMapping("/order/{orderId}")
    public String orderDtl(@PathVariable("orderId") Long orderId, Principal principal, Model model) {
        if (!orderService.validateOrder(orderId, principal.getName())) { // 현재 로그인한 회원이랑 주문한 회원 비교
            return "/";
        }
        Member member = memberService.findByEmail(principal.getName());
        OrderDtlDto orderDtlDto = orderService.getOrderDtl(orderId);

        model.addAttribute("order", orderDtlDto);
        model.addAttribute("member", member);
        return "order/orderDtl";
    }

    @PostMapping(value = "/order")
    // 자바 객체를 HTTP 요청의 body로 전달
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto,
                                              BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) { // orderDto객체에 데이터 바인딩 시 에러 검사
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(),
                    HttpStatus.BAD_REQUEST); // 에러 정보를 ResponseEntity 객체에 담아서 반환
        }

        String email = principal.getName(); // principal 객체에서 현재 로그인한 회원의 이메일 정보를 조회

        Long orderId;

        try {
            orderId = orderService.order(orderDto, email);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    @PostMapping(value = "/grouporder")
    // 자바 객체를 HTTP 요청의 body로 전달
    public @ResponseBody ResponseEntity groupOrder(@RequestBody @Valid OrderDto orderDto,
                                              BindingResult bindingResult, Principal principal) {

        if (bindingResult.hasErrors()) { // orderDto객체에 데이터 바인딩 시 에러 검사
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(),
                    HttpStatus.BAD_REQUEST); // 에러 정보를 ResponseEntity 객체에 담아서 반환
        }

        String email = principal.getName(); // principal 객체에서 현재 로그인한 회원의 이메일 정보를 조회

        Long orderId;

        try {
            orderId = orderService.groupOrder(orderDto, email);
            if(orderId == -1L) new ResponseEntity<String>("자신에게 매칭될 수 없습니다.", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    @PostMapping(value = "/order/{orderId}/complete")
    public @ResponseBody ResponseEntity orderComplete(@PathVariable("orderId") Long orderId, @RequestBody @Valid OrderCplDto orderCplDto,
                                                      BindingResult bindingResult, Principal principal) {

        if (!orderService.validateOrder(orderId, principal.getName())) {
            return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getAllErrors().forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
            return new ResponseEntity<Map<String, String>>(errors,
                    HttpStatus.BAD_REQUEST);
        }

        orderService.orderComplete(orderId, orderCplDto);

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page, Principal principal, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);

        Page<OrderHistDto> ordersHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders", ordersHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);
        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    public ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal) {
        if (!orderService.validateOrder(orderId, principal.getName())) {
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        orderService.cancelOrder(orderId);

        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }


}