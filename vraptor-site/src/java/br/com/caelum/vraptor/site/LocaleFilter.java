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

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

/**
 * Servlet Filter implementation class LocaleFilter
 */
public class LocaleFilter implements Filter {
	private static final Locale PT_BR = new Locale("pt", "br");
	private BiMap<String, String> en2pt;

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;

		String path = req.getContextPath();
		String uri = req.getRequestURI().replaceFirst(path, "");

		request.setAttribute("locale", isEnglish(uri)? Locale.ENGLISH : PT_BR);
		request.setAttribute("path", path);

		if (en2pt.containsKey(uri)) {
			req.getRequestDispatcher(en2pt.get(uri)).forward(request, response);
		} else {
			chain.doFilter(request, response);
		}
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
			.put("/documentation/", "/documentation/")
			.put("/beneficios.jsp", "/benefits.jsp")
			.put("/suporte.jsp", "/support.jsp")
			.put("/vraptor2.jsp", "/en/vraptor2.jsp")
			.build().inverse();
	}

}
