package com.shop.farmmunity.domain.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.farmmunity.domain.item.constant.GroupBuyStatus;
import com.shop.farmmunity.domain.item.entity.Group;
import com.shop.farmmunity.domain.item.repository.GroupRepository;
import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.member.repository.MemberRepository;
import com.shop.farmmunity.domain.order.entity.Order;
import com.shop.farmmunity.domain.order.repository.OrderRepository;
import com.shop.farmmunity.domain.payment.constant.PaymentDtlDto;
import com.shop.farmmunity.domain.payment.entity.Payment;
import com.shop.farmmunity.domain.payment.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;
    private final GroupRepository groupRepository;


    @Value("${custom.toss_secret}")
    private String SECRET_KEY;

    public Order verifyRequest(Long id, Long orderId, Long amount) {

        if (!id.equals(orderId)) { // id가 조작되었는지 검증
            throw new RuntimeException();
        }

        Order order = orderRepository.findById(orderId).orElseThrow(RuntimeException::new);

        if (!order.getTotalPrice().equals(amount)) { // 결제 금액 검증
            throw new RuntimeException();
        }

        return order;
    }

    public ResponseEntity<JsonNode> requestPayment(String paymentKey, String orderId, Long amount) throws JsonProcessingException {

        RestTemplate restTemplate = new RestTemplate(); // 스프링 외부 api 요청/응답 클래스
        HttpHeaders headers = new HttpHeaders(); // 헤더 클래스
        ObjectMapper objectMapper = new ObjectMapper();
        String encodedAuth = new String(Base64.getEncoder().encode((SECRET_KEY + ":").getBytes(StandardCharsets.UTF_8)));
        headers.setBasicAuth(encodedAuth); // Base64로 인코딩한 값을 헤더 설정 정보에 넣음
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        return restTemplate.postForEntity( // 요청하고 JsonNode타입으로 응답받음
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);
    }

    public void payDone(Order order, JsonNode jsonNode) { // 결제 완료하면 결제 정보 데이터베이스에 저장
        order.payDone();
        Payment payment = Payment.createPayment(order, jsonNode);
        if(order.isGroupBuying()){
            checkGroupBuying(order); // 공동구매일 경우 공동구매 그룹 생성 처리를 진행
        }
        paymentRepository.save(payment);
    }

    public void checkGroupBuying(Order order){
        //공동구매라면 결제 완료시 파트너를 매칭시키거나 대기 상태로 변경
        Group group = Group.createHostGroup(order.getMember(), order);
        Group partner = groupRepository.findByItemIdAndStatus(group.getItemId(), GroupBuyStatus.WAIT); // 매칭을 찾는 사람이 있는지 확인
        if(partner != null){
            // 본인이 아닌 공동구매를 대기하는 파트너가 있는 경우
            if(partner.getGroupBuyEndTime().isBefore(LocalDateTime.now())){
                //마감시간이 지난 파트너라면 파트너를 실패상태로 만듦
                partner.setStatus(GroupBuyStatus.FAIL);
                group.setStatus(GroupBuyStatus.WAIT);
            } else{
                //정상 상태일 경우 공동구매를 성공시킴
                group.updateClientGroup(partner.getMember());
                partner.setPartner(group.getMember());
            }
        }else{
            // 대기자가 아무도 없는 경우 대기상태에 들어감
            group.setStatus(GroupBuyStatus.WAIT);
        }
        groupRepository.save(group); // 생성한 공동구매 엔티티를 저장
    }

    @Transactional(readOnly = true)
    public void validatePayment(Long orderId, String email) {

        Member curMember = memberRepository.findByEmail(email); // 현재 로그인한 사용자

        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(EntityNotFoundException::new); // 해당 주문의 유저

        Member savedMember = payment.getMember();
        if (!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) { // 현재 유저와 주문한 유저가 일치하는지
            throw new RuntimeException();
        }
    }

    public PaymentDtlDto getPayment(Long orderId) { // 결제 내역 서비스

        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(EntityNotFoundException::new);
        return PaymentDtlDto.of(payment);
    }
}
