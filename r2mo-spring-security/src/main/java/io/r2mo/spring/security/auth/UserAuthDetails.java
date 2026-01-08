package io.r2mo.spring.security.auth;

import io.r2mo.jaas.element.MSRole;
import io.r2mo.jaas.element.MSUser;
import io.r2mo.jaas.session.UserAt;
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
public class UserAuthDetails implements UserDetails {
    @Getter
    private final UserAt user;
    private final List<GrantedAuthority> authorities = new ArrayList<>();

    public UserAuthDetails(final UserAt user) {
        this.user = user;
        final List<MSRole> roles = user.logged().roles();
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
        final MSUser logged = this.user.logged();
        return Objects.requireNonNull(logged).getPassword();
    }

    @Override
    public String getUsername() {
        final MSUser logged = this.user.logged();
        return Objects.requireNonNull(logged).getUsername();
    }

    public UserAt logged() {
        return this.user;
    }
}
