package br.com.caelum.vraptor.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A cached version of static content handling.
 * 
 * @author guilherme silveira
 */
public class CachedStaticContentHandler implements StaticContentHandler {

	private static final Map<String, Boolean> CACHE = new HashMap<String, Boolean>();
	private final StaticContentHandler delegate;

	public CachedStaticContentHandler(StaticContentHandler delegate) {
		this.delegate = delegate;
	}

	public void deferProcessingToContainer(FilterChain filterChain,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		delegate.deferProcessingToContainer(filterChain, request, response);
	}

	public boolean requestingStaticFile(HttpServletRequest request)
			throws MalformedURLException {
		String uri = uriRelativeToContextRoot(request);
		if (!CACHE.containsKey(uri)) {
			boolean is = delegate.requestingStaticFile(request);
			CACHE.put(uri, is);
		}
		return CACHE.get(uri);
	}

	private String uriRelativeToContextRoot(HttpServletRequest request) {
		return request.getRequestURI().substring(
				request.getContextPath().length());
	}

}
