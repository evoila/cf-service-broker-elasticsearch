package de.evoila.cf.config.security.uaa.handler;

import de.evoila.cf.config.security.uaa.utils.DefaultCorsHeader;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** @author Johannes Hiemer. */
public class CommonCorsAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
						 AuthenticationException exception) throws IOException, ServletException {

		response.addHeader(DefaultCorsHeader.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		response.sendError(HttpServletResponse.SC_FORBIDDEN, exception.getMessage());
	}
	
}
