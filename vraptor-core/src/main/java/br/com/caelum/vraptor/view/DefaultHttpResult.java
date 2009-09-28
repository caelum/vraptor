/**
 *
 */
package br.com.caelum.vraptor.view;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;

/**
 * Implementation that delegates to HttpServletResponse
 * @author Lucas Cavalcanti
 * @author Sergio Lopes
 */
public class DefaultHttpResult implements HttpResult {

	private final HttpServletResponse response;
	private final Router router;
	private final Proxifier proxifier;
	private final HttpServletRequest request;

	public DefaultHttpResult(HttpServletRequest request, HttpServletResponse response, Router router, Proxifier proxifier) {
		this.request = request;
		this.response = response;
		this.router = router;
		this.proxifier = proxifier;
	}

	public HttpResult addDateHeader(String name, long date) {
		response.addDateHeader(name, date);
		return this;
	}

	public HttpResult addHeader(String name, String value) {
		response.addHeader(name, value);
		return this;
	}

	public HttpResult addIntHeader(String name, int value) {
		response.addIntHeader(name, value);
		return this;
	}

	public void sendError(int statusCode) {
		try {
			response.sendError(statusCode);
		} catch (IOException e) {
			throw new ResultException("Error while setting status code", e);
		}

	}

	public HttpResult setStatusCode(int statusCode) {
		response.setStatus(statusCode);
		return this;
	}


	public void movedPermanentlyTo(String uri) {
		this.response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		this.response.addHeader("Location", absoluteURL(uri));
		this.response.addIntHeader("Content-length", 0);
		this.response.addDateHeader("Date", System.currentTimeMillis());
	}

	private String absoluteURL(String uri) {
		if (uri.startsWith("/")) {
			return this.request.getContextPath() + uri;
		} else {
			return uri;
		}
	}

	public <T> T movedPermanentlyTo(final Class<T> controller) {
		return proxifier.proxify(controller, new MethodInvocation<T>() {
			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				String uri = router.urlFor(controller, method, args);
				movedPermanentlyTo(uri);
				return null;
			}
		});
	}
}
