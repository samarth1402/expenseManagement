package com.adobe.prj.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyUserDetails implements UserDetails {

    private String emailId;
    private String password;
    private boolean role;
    private List<GrantedAuthority> authorities;

    public MyUserDetails(User user) {
        this.emailId = user.getEmailId();
        this.password = user.getPassword();
        this.role = user.isManager();
        if(role){
            this.authorities = Arrays.stream(("MANAGER").split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            System.out.println(this.authorities);
        }
        else{
            this.authorities = Arrays.stream(("USER").split(","))
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            System.out.println(this.authorities);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return emailId;
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
        return true;
    }
}
