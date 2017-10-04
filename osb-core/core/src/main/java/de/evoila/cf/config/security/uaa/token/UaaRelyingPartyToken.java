package de.evoila.cf.config.security.uaa.token;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/** @author Johannes Hiemer. */
public class UaaRelyingPartyToken extends AbstractAuthenticationToken {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String token;

    private Object principal;

    public UaaRelyingPartyToken(String token) {
        super(null);
        this.token = token;
    }

    public UaaRelyingPartyToken(Collection<? extends GrantedAuthority> authorities, Object principal) {
        super(authorities);
        this.principal = principal;
        super.setAuthenticated(true);
    }

    public UaaRelyingPartyToken(UserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = userDetails;
        super.setAuthenticated(true);
    }

    public String getToken() {
        return token;
    }

    public Object getCredentials() {
        return null;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public Object getPrincipal() {
        return principal;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }

        super.setAuthenticated(false);
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        super.getAuthorities().addAll(authorities);
    }
}

