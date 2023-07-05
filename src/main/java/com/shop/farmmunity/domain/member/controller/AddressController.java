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
public class AddressController {

    private final MemberService memberService;
    private final AddressService addressService;

    @GetMapping(value = "/members/mypage/address")
    public String addressList(Model model, Principal principal){

        try {
            Member member = memberService.findByEmail(principal.getName());
            List<AddressDto> addressList = addressService.getAddressList(member.getId());
            model.addAttribute("addressList", addressList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/addressForm";
        }
        return "member/addressList";
    }

    @GetMapping(value = "/members/mypage/address/select")
    public String selectAddress(Model model, Principal principal){

        try {
            Member member = memberService.findByEmail(principal.getName());
            List<AddressDto> addressList = addressService.getAddressList(member.getId());
            model.addAttribute("addressList", addressList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/addressForm";
        }
        return "member/selectAddress";
    }

    @GetMapping(value = "/members/mypage/address/select/{selectedId}")
    public @ResponseBody ResponseEntity selectedAddress(@PathVariable Long selectedId, Model model, Principal principal){

        try {
            Member member = memberService.findByEmail(principal.getName());
            Address addressById = addressService.findAddressById(selectedId);
            if (member.getId() != addressById.getMember().getId()) {
                return new ResponseEntity<String>("권한이 없습니다.", HttpStatus.BAD_REQUEST);
            }
            AddressDto address = addressService.getAddress(selectedId);
            return new ResponseEntity<AddressDto>(address, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/members/mypage/address/new")
    public String addressForm(Model model){
        model.addAttribute("addressFormDto", new AddressFormDto());
        return "member/addressForm";
    }

    @PostMapping(value = "/members/mypage/address/new")
    public String newAddress(@Valid AddressFormDto addressFormDto, BindingResult bindingResult, Model model, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "member/addressForm";
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
            return "member/addressForm";
        }

        return "redirect:/members/mypage/address";
    }

    @GetMapping(value = "/members/mypage/address/modify/{addressId}")
    public String modifyAddress(@PathVariable Long addressId, Model model, Principal principal) {

        try {
            Member member = memberService.findByEmail(principal.getName());
            Address addressById = addressService.findAddressById(addressId);
            if (member.getId() != addressById.getMember().getId()) {
                model.addAttribute("errorMessage", "권한이 없습니다.");
                return "redirect:/";
            }
            AddressDto address = addressService.getAddress(addressId);
            model.addAttribute("addressFormDto", address);
            return "member/addressForm";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/members/mypage/address";
        }
    }

    @PostMapping(value = "/members/mypage/address/modify/{addressId}")
    public String modifyAddress(@Valid AddressFormDto addressFormDto, BindingResult bindingResult, Model model, Principal principal){

        try {
            Member member = memberService.findByEmail(principal.getName());
            Address addressById = addressService.findAddressById(addressFormDto.getAddressId());
            if (member.getId() != addressById.getMember().getId()) {
                model.addAttribute("errorMessage", "권한이 없습니다.");
                return "redirect:/";
            }
            if (bindingResult.hasErrors()) {
                return "member/addressForm";
            }
            if (addressFormDto.getIs_default()) {
                addressService.modifyDefaultAddress(member.getId());
            }
            addressService.updateAddress(addressFormDto);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/addressForm";
        }

        return "redirect:/members/mypage/address";
    }

    @GetMapping("/members/mypage/address/delete/{addressId}")
    public String deleteAddress(@PathVariable Long addressId, Principal principal, Model model) {

        try {
            Member member = memberService.findByEmail(principal.getName());
            Address addressById = addressService.findAddressById(addressId);
            if (member.getId() != addressById.getMember().getId()) {
                model.addAttribute("errorMessage", "권한이 없습니다.");
                return "redirect:/";
            }

            addressService.deleteAddress(addressId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/addressForm";
        }

        return "redirect:/members/mypage/address";
    }
}
