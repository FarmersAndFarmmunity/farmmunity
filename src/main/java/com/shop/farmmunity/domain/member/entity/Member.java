package com.shop.farmmunity.domain.member.entity;

import com.shop.farmmunity.base.baseEntity.BaseEntity;
import com.shop.farmmunity.domain.member.constant.Role;
import com.shop.farmmunity.domain.member.dto.MemberFormDto;
import com.shop.farmmunity.domain.member.dto.MemberUpdateRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity {
    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String providerTypeCode;

    private String username;

    @Column(unique = true)
    private String email;

    private String password;

    private String address;

    @Enumerated(EnumType.STRING)
    private Role role;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder) {
        Member member = new Member();
        member.setUsername(memberFormDto.getUsername());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setProviderTypeCode("Farmers");
        member.setRole(Role.ADMIN); // 현재는 멤버의 롤이 기본적으로 ADMIN 으로 설정되어 있다.
        return member;
    }

    public static Member createSocialMember(String providerTypeCode, String username) {
        Member member = new Member();
        member.setUsername(username);
        member.setEmail(username.split("__")[1] + "@" + providerTypeCode.toLowerCase() + ".com");
        member.setAddress("");
        member.setPassword("");
        member.setProviderTypeCode(providerTypeCode);
        member.setRole(Role.ADMIN); // 현재는 멤버의 롤이 기본적으로 ADMIN 으로 설정되어 있다.
        return member;
    }

    public List<? extends GrantedAuthority> getGrantedAuthorities(Role role) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        // 모든 멤버는 ADMIN 권한을 가진다.
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        return grantedAuthorities;
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public void modifyMemberInfo(MemberUpdateRequestDto memberUpdateRequestDto, PasswordEncoder passwordEncoder) {
        this.username = memberUpdateRequestDto.getUsername();
        this.password = passwordEncoder.encode(memberUpdateRequestDto.getConfirmPassword());
    }
}