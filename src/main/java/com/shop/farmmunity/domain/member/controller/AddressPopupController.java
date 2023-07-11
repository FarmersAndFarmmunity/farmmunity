package com.shop.farmmunity.domain.member.controller;

import com.shop.farmmunity.domain.member.dto.AddressDto;
import com.shop.farmmunity.domain.member.dto.AddressFormDto;
import com.shop.farmmunity.domain.member.entity.Address;
import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.member.service.AddressService;
import com.shop.farmmunity.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;
@Controller
@RequiredArgsConstructor
public class AddressPopupController {

    private final MemberService memberService;
    private final AddressService addressService;

    @GetMapping(value = "/members/popup/address/new")
    public String addressForm(Model model){
        model.addAttribute("addressFormDto", new AddressFormDto());
        return "member/popup/addressForm";
    }

    @PostMapping(value = "/members/popup/address/new")
    public String newAddress(@Valid AddressFormDto addressFormDto, BindingResult bindingResult, Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "member/popup/addressForm";
        }

        try {
            Member member = memberService.findByEmail(principal.getName());
            if (addressFormDto.getIs_default()) {
                addressService.modifyDefaultAddress(member.getId());
            }
            Address address = addressFormDto.createAddressForm(member);
            addressService.saveAddress(address);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/popup/addressForm";
        }

        return "redirect:/members/mypage/address/select";
    }

    @GetMapping(value = "/members/popup/address/modify/{addressId}")
    public String modifyAddress(@PathVariable Long addressId, Model model, Principal principal) {

        try {
            Member member = memberService.findByEmail(principal.getName());
            Address addressById = addressService.findAddressById(addressId);
            if (member.getId() != addressById.getMember().getId()) {
                model.addAttribute("errorMessage", "권한이 없습니다.");
                return "redirect:/members/mypage/address/select";
            }
            AddressDto address = addressService.getAddress(addressId);
            model.addAttribute("addressFormDto", address);
            return "member/popup/addressForm";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/members/mypage/address/select";
        }
    }

    @PostMapping(value = "/members/popup/address/modify/{addressId}")
    public String modifyAddress(@Valid AddressFormDto addressFormDto, BindingResult bindingResult, Model model, Principal principal){

        try {
            Member member = memberService.findByEmail(principal.getName());
            Address addressById = addressService.findAddressById(addressFormDto.getAddressId());
            if (member.getId() != addressById.getMember().getId()) {
                model.addAttribute("errorMessage", "권한이 없습니다.");
                return "redirect:/members/mypage/address/select";
            }
            if (bindingResult.hasErrors()) {
                return "member/popup/addressForm";
            }
            if (addressFormDto.getIs_default()) {
                addressService.modifyDefaultAddress(member.getId());
            }
            addressService.updateAddress(addressFormDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/popup/addressForm";
        }

        return "redirect:/members/mypage/address/select";
    }

    @GetMapping("/members/popup/address/delete/{addressId}")
    public String deleteAddress(@PathVariable Long addressId, Principal principal, Model model) {

        try {
            Member member = memberService.findByEmail(principal.getName());
            Address addressById = addressService.findAddressById(addressId);
            if (member.getId() != addressById.getMember().getId()) {
                model.addAttribute("errorMessage", "권한이 없습니다.");
                return "redirect:/members/mypage/address/select";
            }

            addressService.deleteAddress(addressId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/popup/addressForm";
        }

        return "redirect:/members/mypage/address/select";
    }
}
