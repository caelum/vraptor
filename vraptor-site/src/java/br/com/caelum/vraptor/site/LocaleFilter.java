package br.com.caelum.vraptor.site;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

/**
 * Servlet Filter implementation class LocaleFilter
 */
public class LocaleFilter implements Filter {
	private static final String VRAPTOR_LOCALE = "VRAPTOR_LOCALE";
	private static final Locale PT_BR = new Locale("pt", "BR");
	private BiMap<String, String> en2pt;

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		String path = req.getContextPath();
		String uri = req.getRequestURI().replaceFirst(path, "");
		HttpServletResponse res = (HttpServletResponse) response;

		if (uri.equals("/")) {
			Locale locale = getLocale(req);
			if (locale == null) {
				String accept = req.getHeader("accept-language");
				if (accept != null && accept.startsWith("en")) {
					setLocale(res, Locale.ENGLISH);
					res.sendRedirect(req.getContextPath() + "/en");
					return;
				}
				setLocale(res, PT_BR);
				locale = PT_BR;
			} else if (locale.equals(Locale.ENGLISH)) {
				res.sendRedirect(req.getContextPath() + "/en");
				return;
			}
			request.setAttribute("locale", PT_BR);
		} else if (uri.equals("/pt") || uri.equals("/pt/")){
			setLocale(res, PT_BR);
			res.sendRedirect(req.getContextPath() + "/");
			return;
		}

		request.setAttribute("locale", isEnglish(uri)? Locale.ENGLISH : PT_BR);
//		req.getSession().setAttribute(VRAPTOR_LOCALE, req.getAttribute("locale"));

		request.setAttribute("path", path);

		if (en2pt.containsKey(uri)) {
			req.getRequestDispatcher(en2pt.get(uri)).forward(request, response);
		} else {
			chain.doFilter(request, response);
		}
	}

	private void setLocale(HttpServletResponse res, Locale locale) {
		Cookie cookie = new Cookie(VRAPTOR_LOCALE, locale.toString());
		cookie.setPath("/");
		res.addCookie(cookie);
	}

	private Locale getLocale(HttpServletRequest req) {
		for (Cookie cookie : req.getCookies()) {
			if (cookie.getName().equals(VRAPTOR_LOCALE)) {
				if (cookie.getValue().equals(Locale.ENGLISH.toString()))
					return Locale.ENGLISH;
				else
					return PT_BR;
			}
		}
		return null;
	}

	private boolean isEnglish(String uri) {
		return en2pt.containsKey(uri) || uri.startsWith("/documentation");
	}

	public void init(FilterConfig fConfig) throws ServletException {
		en2pt = ImmutableBiMap.<String,String>builder()
			.put("/", "/en/")
			.put("/header.jsp", "/en/header.jsp")
			.put("/footer.jsp", "/en/footer.jsp")
			.put("/index.jsp", "/en")
			.put("/download.jsp", "/en/download.jsp")
			.put("/equipe.jsp", "/team.jsp")
			.put("/documentation/", "/documentation/")
			.put("/beneficios.jsp", "/benefits.jsp")
			.put("/suporte.jsp", "/support.jsp")
			.put("/vraptor2.jsp", "/en/vraptor2.jsp")
			.build().inverse();
	}

}
