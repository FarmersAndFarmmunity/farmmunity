package com.shop.farmmunity.domain.item.controller;

import com.shop.farmmunity.domain.item.dto.ItemFormDto;
import com.shop.farmmunity.domain.item.dto.ItemSearchDto;
import com.shop.farmmunity.domain.item.entity.Item;
import com.shop.farmmunity.domain.item.service.ItemService;
import com.shop.farmmunity.domain.review.entity.Review;
import com.shop.farmmunity.domain.review.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ItemController {
    @Value("${custom.postForPage}")
    private int postForPage;

    private final ItemService itemService;

    private final ReviewService reviewService;

    @GetMapping(value = "/vendor/item/new")
    public String itemForm(Model model) {
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    @PostMapping(value = "/vendor/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList) {

        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/vendor/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            model.addAttribute("itemFormDto", itemFormDto);
        } catch (EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }


    //관리자용 전체 상품 관리 페이지 조회
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String adminItemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model) {
        String email = "ADMIN"; // 관리자의 경우 이메일 조회 없이 넘김
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, postForPage); // 한 페이지에 표시될 수 지정
        model.addAttribute("itemFormDto", new ItemFormDto());

        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }

    //판매자용 내 상품 관리 페이지 조회
    @GetMapping(value = {"/vendor/items", "/vendor/items/{page}"})
    public String myItemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model, Principal principal) {
        String email = principal.getName(); // 유저의 이메일 정보

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, postForPage);
        model.addAttribute("itemFormDto", new ItemFormDto());

        Page<Item> items = itemService.getMyItemPage(itemSearchDto, pageable, email);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "item/itemMng";
    }

    // 수정 기능
    @PostMapping(value = "/vendor/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model) {

        if (bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        if (itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    // 삭제 기능
    @PostMapping("/vendor/item/delete/{itemId}")
    public String deleteItem(@PathVariable Long itemId, Model model) throws Exception {
        try {
            itemService.deleteItem(itemId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 삭제 중 에러가 발생했습니다.");
            return "item/itemForm";
        }
        return "redirect:/";
    }

    @GetMapping("/item/{itemId}")
    public String itemDtl(Model model, @PathVariable Long itemId) {
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        List<Review> reviewList = reviewService.getList(itemId);

        model.addAttribute("reviews", reviewList);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }
}
