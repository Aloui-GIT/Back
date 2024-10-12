package com.example.generateurformulaire.security;

import com.example.generateurformulaire.AppUser.User;
import com.example.generateurformulaire.AppUser.UserService;
import com.example.generateurformulaire.Utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Lazy))
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        Set<GrantedAuthority> authorities = Set.of(SecurityUtils.convertToAuthority(user.getRole().name()));

        return UserPrincipal.builder()
                .user(user)
                .id(user.getUserId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
