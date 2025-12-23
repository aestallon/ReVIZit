package org.revizit.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "user_account")
@Getter
@Setter
public class UserAccount implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "mail_addr", nullable = false)
    private String mailAddr;

    @Column(name = "user_pw", nullable = false)
    private String userPw;

    @Column(name = "user_role", nullable = false)
    private String userRole;

    @Column(nullable = false)
    private boolean inactive;

    @Column(name = "profile_id")
    private Integer profileId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + userRole));
    }

    @Override
    public String getPassword() {
        return userPw;
    }

    @Override
    public boolean isEnabled() {
        return !inactive;
    }
}
