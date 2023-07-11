package com.shop.farmmunity.domain.member.service;

import com.shop.farmmunity.domain.member.dto.MemberSearchDto;
import com.shop.farmmunity.domain.member.dto.MemberUpdateRequestDto;
import com.shop.farmmunity.domain.member.entity.Member;
import com.shop.farmmunity.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail());

        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    // 소셜 로그인
    private Member join(String providerTypeCode, String username, String password) throws UsernameNotFoundException {
        if (findByUsername(username).isPresent()) {
            throw new UsernameNotFoundException(username);
        }
        Member member = Member.createSocialMember(providerTypeCode, username);

        return memberRepository.save(member);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    // 소셜 로그인 시 실행되는 함수
    public Member whenSocialLogin(String providerTypeCode, String username) {
        Optional<Member> opMember = findByUsername(username);

        if (opMember.isPresent())
            return opMember.get();

        // 소셜 로그인를 통한 가입 시 비밀번호는 없다.
        return join(providerTypeCode, username, ""); // 최초 로그인 시 딱 한번 실행
    }

    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Page<Member> getAdminMemberPage(MemberSearchDto memberSearchDto, Pageable pageable) {
        return memberRepository.getAdminMemberPage(memberSearchDto, pageable);
    }

    @Transactional
    public void updateMember(MemberUpdateRequestDto memberUpdateRequestDto, String username) {



    }



}
