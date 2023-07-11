package com.shop.farmmunity.domain.member.controller;

import com.shop.farmmunity.base.security.CustomUserDetailsService;
import com.shop.farmmunity.domain.member.constant.Role;
import com.shop.farmmunity.domain.member.dto.MemberFormDto;
import com.shop.farmmunity.domain.member.dto.MemberSearchDto;
import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;


@Controller
@RequiredArgsConstructor
public class MemberController {
    private final CustomUserDetailsService customUserDetailsService;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    @Value("${custom.postForPage}")
    private int postForPage;

    @GetMapping(value = "/members/new")
    public String memberForm(Model model) {
        model.addAttribute("memberFormDto", new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/members/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "member/memberForm";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = "/members/login")
    public String loginMember() {
        return "member/memberLoginForm";
    }

    @GetMapping(value = "/members/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "member/memberLoginForm";
    }

    // 멤버 관리 기능

    @GetMapping(value = {"/admin/member", "/admin/member/{page}"})
    public String memberList(MemberSearchDto memberSearchDto, @PathVariable("page") Optional<Integer> page, Model model) throws Exception {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, postForPage); // 페이지에 표시될 최대 숫자

        Page<Member> members = memberService.getAdminMemberPage(memberSearchDto, pageable);
        model.addAttribute("members", members);
        model.addAttribute("memberSearchDto", memberSearchDto);
        model.addAttribute("maxPage", 5);

        return "member/memberList";
    }

    // 멤버 등급 수정
    @Transactional
    @PostMapping(value = "/admin/member/update/{id}")
    public String memberUpdate(@PathVariable("id") Long id, HttpServletRequest request, Model model) {
        String paramRole = request.getParameter("paramRole");
        Role role = (paramRole.equals("ADMIN")) ? Role.ADMIN : (paramRole.equals("VENDOR")) ? Role.VENDOR : Role.USER;

        try {
            Member member = memberService.getMemberById(id).get();
            member.updateRole(role); // db상 권한 갱신
            Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // 유저의 인증정보를 가져와서 저장(GET)
            User user = (User) auth.getPrincipal(); // 유저의 정보를 저장
            SecurityContextHolder.getContext().setAuthentication(createNewAuthentication(auth, user.getUsername())); // 새 정보를 가져와서 갱신(SET)
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/";
        }

        return "redirect:/";
    }

    protected Authentication createNewAuthentication(Authentication currentAuth, String username) {
        UserDetails newPrincipal = customUserDetailsService.loadUserByUsername(username);
        UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(newPrincipal, currentAuth.getCredentials(), newPrincipal.getAuthorities());
        newAuth.setDetails(currentAuth.getDetails());
        return newAuth;
    }

}
