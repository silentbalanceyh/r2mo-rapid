package io.r2mo.spring.security.auth;

import io.r2mo.jaas.element.MSRole;
import io.r2mo.jaas.element.MSUser;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author lang : 2025-11-11
 */
public class MSUserDetail implements UserDetails {
    @Getter
    private final MSUser user;
    private final List<GrantedAuthority> authorities = new ArrayList<>();

    public MSUserDetail(final MSUser user) {
        this.user = user;
        final List<MSRole> roles = user.roles();
        roles.forEach(role -> {
            final GrantedAuthority authority = new SimpleGrantedAuthority(role.getName());
            this.authorities.add(authority);
        });
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return Objects.requireNonNull(this.user).getPassword();
    }

    @Override
    public String getUsername() {
        return Objects.requireNonNull(this.user).getUsername();
    }
}
