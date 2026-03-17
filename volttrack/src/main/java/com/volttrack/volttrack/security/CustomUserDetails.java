package com.volttrack.volttrack.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final Long id;              // internal DB id
    private final String publicId;      // ✅ NEW (IMPORTANT)
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean active;

    // ✅ UPDATED CONSTRUCTOR
    public CustomUserDetails(Long id,
                             String publicId,
                             String username,
                             String password,
                             Collection<? extends GrantedAuthority> authorities,
                             boolean active) {
        this.id = id;
        this.publicId = publicId;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.active = active;
    }

    // ✅ GETTERS
    public Long getId() {
        return id;
    }

    public String getPublicId() {   // ✅ REQUIRED
        return publicId;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}