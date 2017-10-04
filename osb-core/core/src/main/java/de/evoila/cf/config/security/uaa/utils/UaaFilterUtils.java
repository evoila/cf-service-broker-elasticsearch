package de.evoila.cf.config.security.uaa.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.evoila.cf.config.security.uaa.provider.UaaRelyingPartyAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/** @author Johannes Hiemer. */
public class UaaFilterUtils {

    private static final String BEARER = "Bearer ";
    private static Logger log = LoggerFactory.getLogger(UaaFilterUtils.class);

    public static ObjectMapper objectMapper = new ObjectMapper();

    public static String tryResolveToken(HttpServletRequest request, String headerName) {
        Assert.notNull(headerName, "headerName must not be null/or empty");
        String headerValue = request.getHeader(headerName);
        if (headerValue == null || !headerValue.startsWith(BEARER)) {
            return null;
        }

        return headerValue.substring(BEARER.length());
    }

    @NotNull
    public static Map<String, Object> verifiedToken(String token, String publicKey) {
        Jwt jwt = JwtHelper.decode(token);

        // Currently not sure how we should handle this because we have multiple
        // CF instances. We would need to have a central file for all UAA
        // instances
        // verifySignature(jwt, publicKey);

        Map<String, Object> tokenObj = tryExtractToken(jwt);
        if (tokenObj == null) {
            throw new AuthenticationServiceException("Error parsing JWT token/extracting claims");
        }

        verifyExpiration(tokenObj);
        return tokenObj;
    }

    private static void verifyExpiration(Map<String, Object> tokenObj) {
        Long timestamp = (long) ((Integer) tokenObj.get(UaaRelyingPartyAuthenticationProvider.Properties.EXP)) * 1000;
        Date now = new Date();
        Date expirationTime = new Date(timestamp);
        if (!now.before(expirationTime)) {
            throw new AuthenticationServiceException("Token expiration timed out");
        }
    }

    private static void verifySignature(Jwt jwt, String publicKey) {
        try {
            RsaVerifier rsaVerifier = new RsaVerifier(publicKey);
            jwt.verifySignature(rsaVerifier);
        } catch (Exception ex) {
            throw new AuthenticationServiceException("Error verifying signature of token");
        }
    }

    public static Map<String, Object> tryExtractToken(Jwt jwt) {
        if (jwt.getClaims() == null)
            return null;

        try {
            return objectMapper.readValue(jwt.getClaims(), new TypeReference<HashMap<String, Object>>() {});
        } catch (IOException e) {
            log.error("Error parsing claims from JWT", e);
        }

        return null;
    }
}
