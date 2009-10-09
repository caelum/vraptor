package br.com.caelum.vraptor.view;

import static br.com.caelum.vraptor.view.Results.page;
import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.test.MockResult;

public class DefaultRefererResult implements RefererResult {

	private final MutableRequest request;
	private final Result result;
	private final Router router;

	public DefaultRefererResult(Result result, MutableRequest request, Router router) {
		this.result = result;
		this.request = request;
		this.router = router;
	}
	public FallbackResult forward() {
		String referer = getReferer();
		if (referer == null) {
			return new DefaultFallbackResult();
		}
		ResourceMethod method = router.parse(referer, HttpMethod.GET, request);
		if (method == null) {
			result.use(page()).forward(referer);
		} else {
			Object instance = result.use(page()).of(method.getResource().getType());
			new Mirror().on(instance).invoke().method(method.getMethod())
				.withArgs(new Object[method.getMethod().getParameterTypes().length]);
		}
		return new NullFallbackResult();
	}

	public FallbackResult redirect() {
		String referer = getReferer();
		if (referer == null) {
			return new DefaultFallbackResult();
		}
		return new NullFallbackResult();
	}

	private String getReferer() {
		String referer = request.getHeader("Referer");
		if (referer == null) {
			return null;
		}

		String path = request.getContextPath();
		return referer.substring(referer.indexOf(path) + path.length());
	}

	private static class NullFallbackResult implements FallbackResult{
		public <T extends View> T or(Class<T> view) {
			return new MockResult().use(view);
		}
	}
	private class DefaultFallbackResult implements FallbackResult {
		public <T extends View> T or(Class<T> view) {
			return result.use(view);
		}
	}
}
