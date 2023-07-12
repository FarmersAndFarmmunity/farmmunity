package com.shop.farmmunity.domain.item.controller;

import com.shop.farmmunity.base.security.CustomUserDetailsService;
import com.shop.farmmunity.domain.item.dto.GroupBuyDto;
import com.shop.farmmunity.domain.item.dto.ItemFormDto;
import com.shop.farmmunity.domain.item.dto.ItemOptionDto;
import com.shop.farmmunity.domain.item.dto.ItemSearchDto;
import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.item.entity.ItemOption;
import com.shop.farmmunity.domain.item.service.ItemOptionService;
import com.shop.farmmunity.domain.item.service.ItemService;
import com.shop.farmmunity.domain.itemTag.entity.ItemTag;
import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.payment.constant.PaymentDtlDto;
import com.shop.farmmunity.domain.review.entity.Review;
import com.shop.farmmunity.domain.review.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    @Value("${custom.postForPage}")
    private int postForPage;

    private final ItemService itemService;

    private final ItemOptionService itemOptionService;

    private final ReviewService reviewService;

    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping(value = "/vendor/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        model.addAttribute("itemOptionDto", new ItemOptionDto());
        return "item/itemForm";
    }

    @PostMapping(value = "/vendor/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model,
                          @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        if (bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList, itemFormDto.getItemTagContents());
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }


    //관리자용 상품 관리 페이지 조회
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String adminItemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model){
        String email = "ADMIN"; // 관리자의 경우 이메일 조회 없이 넘김
        Pageable pageable = PageRequest.of(page.isPresent()? page.get() : 0, postForPage); // 한 페이지에 표시될 수 지정
        model.addAttribute("itemFormDto", new ItemFormDto());

        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }

    // 상품 등록 정보 조회
    @GetMapping(value = {"/vendor/item/{itemId}", "/admin/item/{itemId}"})
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model, Principal principal){
        try {
            checkAuthority(itemId, principal); // 작성자 본인이거나 관리자가 아니면 예외 처리
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch(EntityNotFoundException e){
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

    @DeleteMapping(value = {"/vendor/item/itemOption/{itemOptionId}", "/admin/item/itemOption/{itemOptionId}"})
    public @ResponseBody ResponseEntity deleteItemOption(@PathVariable Long itemOptionId, Principal principal) {

        try {
            ItemOption itemOption = itemOptionService.findByItemOptionId(itemOptionId);
            Long itemId = itemOption.getItem().getId();
            checkAuthority(itemId, principal);
            itemOptionService.deleteItemOption(itemOptionId);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    // 수정 기능
    @PostMapping(value = { "/vendor/item/{itemId}", "/admin/item/{itemId}"})
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam(name = "itemImgFile") List<MultipartFile> itemImgFileList, Model model){

        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList, itemFormDto.getItemTagContents());
        } catch (Exception e){
            log.error("상품 등록 중 에러가 발생했습니다. ", e);
            model.addAttribute("errorMessage", e.getMessage());
            return "item/itemForm";
        }

        return "redirect:/";
    }

    // 삭제 기능
    @PostMapping(value = {"/vendor/item/delete/{itemId}", "/admin/item/delete/{itemId}"})
    public String deleteItem(@PathVariable Long itemId, Model model, Principal principal) throws Exception {
        try {
            checkAuthority(itemId, principal); // 작성자 본인이거나 관리자가 아니면 예외 처리
            itemService.deleteItem(itemId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 삭제 중 에러가 발생했습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }

    @GetMapping("/item/{itemId}")
    public String itemDtl(Model model, @PathVariable Long itemId) {
        Optional<Item> item = itemService.findById(itemId);
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        List<Review> reviewList = reviewService.getList(itemId);
        GroupBuyDto groupBuyDto = itemService.getGroupBuyInfo(itemId);

        model.addAttribute("reviews", reviewList);
        model.addAttribute("item", itemFormDto);
        model.addAttribute("groupBuyInfo", groupBuyDto);
        return "item/itemDtl";
    }

    @GetMapping("/item/{itemId}/groupBuyList") // 공동구매 매칭 성공 목록
    public String paymentDtl(Model model, @PathVariable Long itemId, Principal principal) {
        List<GroupBuyDto> groupBuyDtos = itemService.getGroupBuyList(itemId);

        model.addAttribute("groups", groupBuyDtos);

        return "item/groupBuyList";
    }

    public boolean checkAuthority(Long itemId, Principal principal){
        UserDetails user = customUserDetailsService.loadUserByUsername(principal.getName());
        if(user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")) || itemService.findById(itemId).get().getCreatedBy().equals(principal.getName())){
            return true;
        }
        throw new AccessDeniedException("접근 권한이 없습니다.");
    }

    @GetMapping("/item/tag/{tagContent}")
    public String tagList(Model model, @PathVariable String tagContent) {
        List<Item> itemTags = itemService.getItemTags(tagContent);

        model.addAttribute("items", itemTags);
        return "item/tagList";
    }
}
