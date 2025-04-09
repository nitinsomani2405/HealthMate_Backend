package com.auth.Models;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;

@Getter
public class UserPrinciple implements UserDetails {

    private UUID id;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String gender;
    private String role;
    private boolean enabled;

    public UserPrinciple(UUID id, String email, String password, String fullName, String phoneNumber, String gender, String role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.role = role;
        this.enabled = true;  
    }

    public static UserPrinciple build(user user) {
        return new UserPrinciple(
            user.getId(),
            user.getEmail(),
            user.getPassword(),
            user.getFullName(),
            user.getPhoneNumber(),
            user.getGender(),
            user.getRole()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
