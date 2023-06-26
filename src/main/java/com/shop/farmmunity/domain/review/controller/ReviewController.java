package com.shop.farmmunity.domain.review.controller;

import com.shop.farmmunity.domain.item.dto.ItemFormDto;
import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.item.service.ItemService;
import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.member.service.MemberService;
import com.shop.farmmunity.domain.review.dto.ReviewFormDto;
import com.shop.farmmunity.domain.review.dto.ReviewSearchDto;
import com.shop.farmmunity.domain.review.entity.Review;
import com.shop.farmmunity.domain.review.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ItemService itemService;

    private final MemberService memberService;

    @Transactional(readOnly = true)
    @GetMapping(value = {"/reviews/new"})
    public String showReviewForm(Long itemId, Long orderId, Principal principal, Model model) {
        String email = principal.getName(); // 로그인한 회원 정보
        // 해당 회원이 상품을 실제 구매를 한 회원인지 체크해야 함
        if (!(reviewService.validateOrder(email, orderId, itemId))) {
            return "redirect:/orders"; // 권한이 없으면 구매이력으로 매핑
        }

        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        model.addAttribute("item", itemFormDto);
        model.addAttribute("reviewFormDto", new ReviewFormDto());

        return "review/reviewForm";
    }

    @Transactional
    @PostMapping(value = {"/reviews/new/{itemId}"})
    public String createReview(@Valid ReviewFormDto reviewFormDto, BindingResult bindingResult,
                               Principal principal, Model model, @PathVariable Long itemId) {
        String email = principal.getName();
        Member member = memberService.findByEmail(email);

        Item item = itemService.findById(itemId).orElseThrow(EntityNotFoundException::new);
        reviewFormDto.setItem(item);

        if (bindingResult.hasErrors()) {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("item", itemFormDto);
            return "review/reviewForm";
        }

        try {
            reviewService.saveReview(reviewFormDto, member);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "리뷰 등록 중 에러가 발생하였습니다.");
            return "review/reviewForm";
        }

        return "redirect:/";
    }

    @Transactional(readOnly = true)
    @GetMapping(value = {"/reviews", "/reviews/{page}"})
    public String showAllReview(ReviewSearchDto reviewSearchDto, @PathVariable("page") Optional<Integer> page, Model model, Principal principal) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
        model.addAttribute("itemFormDto", new ItemFormDto());

        String email = principal.getName();

        Page<Review> reviews = reviewService.getReviewPage(reviewSearchDto, pageable, email);
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewSearchDto", reviewSearchDto);
        model.addAttribute("maxPage", 5);

        return "review/reviewMng";
    }

    @Transactional
    @GetMapping("/reviews/delete/{reviewId}")
    public String deleteReview(@PathVariable("reviewId") Long reviewId) {
        reviewService.deleteReview(reviewId);
        return "redirect:/reviews";
    }
}
