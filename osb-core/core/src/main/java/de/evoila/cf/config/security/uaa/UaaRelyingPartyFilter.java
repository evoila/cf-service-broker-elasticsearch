package de.evoila.cf.config.security.uaa;

import de.evoila.cf.config.security.uaa.token.UaaRelyingPartyToken;
import de.evoila.cf.config.security.uaa.utils.UaaFilterUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** @author Johannes Hiemer. */
public class UaaRelyingPartyFilter extends GenericFilterBean {

    private AuthenticationManager authenticationManager;

    private RequestMatcher requiresAuthenticationRequestMatcher;

    private AuthenticationSuccessHandler successHandler =
            new SavedRequestAwareAuthenticationSuccessHandler();
    private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    public UaaRelyingPartyFilter(AuthenticationManager authenticationManager) {
        setFilterProcessesUrl("/v2/manage/**");
        this.setAuthenticationManager(authenticationManager);
    }

    public AuthenticationSuccessHandler getSuccessHandler() {
        return successHandler;
    }

    public void setSuccessHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    public AuthenticationFailureHandler getFailureHandler() {
        return failureHandler;
    }

    public void setFailureHandler(AuthenticationFailureHandler failureHandler) {
        this.failureHandler = failureHandler;
    }

    private void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    public void setFilterProcessesUrl(String filterProcessesUrl) {
        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(filterProcessesUrl));
    }

    public final void setRequiresAuthenticationRequestMatcher(RequestMatcher requestMatcher) {
        Assert.notNull(requestMatcher, "requestMatcher cannot be null");
        this.requiresAuthenticationRequestMatcher = requestMatcher;
    }

    protected boolean requiresAuthentication(
            HttpServletRequest request, HttpServletResponse response) {
        return requiresAuthenticationRequestMatcher.matches(request);
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        boolean isOption = request.getMethod().equals(HttpMethod.OPTIONS.toString());
        if (isOption || !requiresAuthentication(request, response)) {
            chain.doFilter(request, response);
            return;
        }

        // We need to handle the tokens here, check the implementation how to resovle it
        String token = UaaFilterUtils.tryResolveToken(request, HttpHeaders.AUTHORIZATION);
        try {

            if (token == null) {
                throw new AuthenticationCredentialsNotFoundException("No authorization header present.");
            }

            Authentication authResult =
                    this.authenticationManager.authenticate(new UaaRelyingPartyToken(token));
            if (authResult != null) {
                successfulAuthentication(request, response, chain, authResult);
                return;
            }

        } catch (InternalAuthenticationServiceException ex) {
            logger.error("An internal error occurred while trying to authenticate the user.", ex);
            unsuccessfulAuthentication(request, response, chain, ex);
        } catch (AuthenticationException ex) {
            unsuccessfulAuthentication(request, response, chain, ex);
        }
    }

    private void unsuccessfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            AuthenticationException failed)
            throws IOException, ServletException {
        SecurityContextHolder.clearContext();

        if (logger.isDebugEnabled()) {
            logger.debug("Authentication request failed: " + failed.toString());
            logger.debug("Updated SecurityContextHolder to contain null Authentication");
            logger.debug("Delegating to authentication failure handler " + failureHandler);
        }

        failureHandler.onAuthenticationFailure(request, response, failed);
    }

    private void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult)
            throws IOException, ServletException {

        if (logger.isDebugEnabled()) {
            logger.debug(
                    "Authentication success. Updating SecurityContextHolder to contain: " + authResult);
        }

        SecurityContextHolder.getContext().setAuthentication(authResult);

        successHandler.onAuthenticationSuccess(request, response, authResult);

        chain.doFilter(request, response);
    }
}
