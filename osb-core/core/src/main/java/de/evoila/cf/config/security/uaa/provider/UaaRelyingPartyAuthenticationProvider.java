/** */
package de.evoila.cf.config.security.uaa.provider;

import de.evoila.cf.config.security.uaa.ScopeAuthority;
import de.evoila.cf.config.security.uaa.UaaUserDetails;
import de.evoila.cf.config.security.uaa.token.UaaRelyingPartyToken;
import de.evoila.cf.config.security.uaa.utils.UaaFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** @author Johannes Hiemer. */
public class UaaRelyingPartyAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(UaaRelyingPartyAuthenticationProvider.class);

    public static class Properties {

        // These claims are always present (regardless of scope)
        public static final String EXP = "exp";
        public static final String CLIENT = "client";
        public static final String ORIGIN = "origin";
        public static final String SCOPE = "scope";
        public static final String SUB = "sub";
        public static final String USER_NAME = "user_name";

    }

    private String publicKey = null;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    public void afterPropertiesSet() throws Exception {
        //Assert.notNull(this.publicKey, "The publicKey must be set");
    }

    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

        if (!supports(authentication.getClass())) {
            return null;
        }

        UaaRelyingPartyToken auth = (UaaRelyingPartyToken) authentication;
        Map<String, Object> tokenObj = UaaFilterUtils.verifiedToken(auth.getToken(), publicKey);

        UaaUserDetails userDetails = new UaaUserDetails();
        userDetails.setUsername(tokenObj.get(Properties.USER_NAME).toString());
        userDetails.setGrantedAuthorities(scopeToGrantedAuthority((List<String>) tokenObj.get(Properties.SCOPE)));

        if (!userDetails.isEnabled()) {
            throw new AuthenticationServiceException("User is disabled");
        }

        return createSuccessfulAuthentication(userDetails);
    }

    private List<GrantedAuthority>  scopeToGrantedAuthority(List<String> scopes) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (String scope : scopes) {
            grantedAuthorities.add(new ScopeAuthority(scope));
        }

        return grantedAuthorities;
    }

    protected Authentication createSuccessfulAuthentication(UserDetails userDetails) {
        return new UaaRelyingPartyToken(userDetails, authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));
    }

    public boolean supports(Class<?> authentication) {
        return UaaRelyingPartyToken.class.isAssignableFrom(authentication);
    }

    public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
    }

}
