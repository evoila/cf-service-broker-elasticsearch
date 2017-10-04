package de.evoila.cf.config.security.uaa;

import org.springframework.security.core.GrantedAuthority;

/** @author Johannes Hiemer. */
public class ScopeAuthority implements GrantedAuthority {

    private String authority;

    public ScopeAuthority(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }
}
