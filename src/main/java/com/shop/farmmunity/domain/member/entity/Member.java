package com.shop.farmmunity.domain.member.entity;

import com.shop.farmmunity.base.baseEntity.BaseEntity;
import com.shop.farmmunity.domain.member.constant.Role;
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
@Table(name="member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity {
    @Id
    @Column(name="member_id")
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

    public List<? extends GrantedAuthority> getGrantedAuthorities(Role role) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        // 모든 멤버는 ADMIN 권한을 가진다.
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));

        return grantedAuthorities;
    }

    public void updateRole(Role role){
        this.role = role;
    }
}