package de.evoila.cf.config.security.uaa.utils;

import javax.servlet.http.HttpServletRequest;

/** @author Johannes Hiemer. */
public class DefaultCorsHeader {

    public static String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    public static String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    public static String WWW_AUTHENTICATE = "WWW-Authenticate";
	
	public static String getBaseUrl(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
	}
}
