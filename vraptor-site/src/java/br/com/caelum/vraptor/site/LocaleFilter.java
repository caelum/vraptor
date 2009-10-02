package br.com.caelum.vraptor.site;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet Filter implementation class LocaleFilter
 */
public class LocaleFilter implements Filter {
	private static final Locale PT_BR = new Locale("pt", "br");

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		String path = req.getContextPath();
		String uri = req.getRequestURI().replaceFirst(path, "");

		response.setLocale(uri.startsWith("/en")? Locale.ENGLISH : PT_BR);
		request.setAttribute("contextPath", uri.startsWith("/en")? path + "/en" : path);

		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {
	}

}
