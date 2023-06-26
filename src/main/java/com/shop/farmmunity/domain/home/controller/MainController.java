package com.shop.farmmunity.domain.home.controller;

import com.shop.farmmunity.base.security.CustomUserDetailsService;
import com.shop.farmmunity.domain.item.constant.ItemClassifyStatus;
import com.shop.farmmunity.domain.item.dto.ItemClassifyDto;
import com.shop.farmmunity.domain.item.dto.ItemSearchDto;
import com.shop.farmmunity.domain.item.dto.MainItemDto;
import com.shop.farmmunity.domain.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ItemService itemService;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping(value = {"/", "/classify/{itemClassifyStatus}"})
    public String main(ItemClassifyDto itemClassifyDto, ItemSearchDto itemSearchDto, Optional<Integer> page, Model model, Principal principal, @PathVariable("itemClassifyStatus") Optional<ItemClassifyStatus> itemClassifyStatus) {
        itemClassifyStatus.ifPresent(itemClassifyDto::setItemClassifyStatus); //URL의 itemClassifyStatus 값이 존재할 때 실행

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 8);
        Page<MainItemDto> items = itemService.getMainItemPage(itemClassifyDto, itemSearchDto, pageable);
        model.addAttribute("items", items);
        model.addAttribute("itemClassifyDto", itemClassifyDto);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        if (principal != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // 유저의 인증정보를 가져와서 저장(GET)
            User user = (User) auth.getPrincipal(); // 유저의 정보를 저장
            SecurityContextHolder.getContext().setAuthentication(createNewAuthentication(auth, user.getUsername())); // 새 정보를 가져와서 갱신(SET)
        }

        return "main";
    }

    // 권한 조회
    protected Authentication createNewAuthentication(Authentication currentAuth, String username) {
        UserDetails newPrincipal = customUserDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(newPrincipal, currentAuth.getCredentials(), newPrincipal.getAuthorities());
        newAuth.setDetails(currentAuth.getDetails());
        return newAuth;
    }
}
