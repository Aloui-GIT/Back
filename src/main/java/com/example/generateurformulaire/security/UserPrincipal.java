package com.example.generateurformulaire.security;

import com.example.generateurformulaire.AppUser.User;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.Set;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserPrincipal implements UserDetails, Principal
{


    Long id;
    String username;
    transient  String password; //don't show up on serialized places.
    transient User user; //user for only login operation, don't use in JWT.
    private Set<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {

        return authorities;
    }

    @Override
    public String getPassword()
    {

        return password;
    }

    @Override
    public String getUsername()
    {

        return username;
    }

    @Override
    public boolean isAccountNonExpired()
    {

        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()
    {

        return true;
    }

    @Override
    public boolean isEnabled()
    {

        return true;
    }

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
}
