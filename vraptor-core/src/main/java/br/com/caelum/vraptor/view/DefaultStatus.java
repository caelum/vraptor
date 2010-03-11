package br.com.caelum.vraptor.view;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.EnumSet;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.resource.HttpMethod;

/**
 * Allows header related results.
 *
 * @author guilherme silveira
 * @since 3.0.3
 */
@Component
public class DefaultStatus implements Status {

	private final HttpServletResponse response;
	private final Result result;
	private final Configuration config;
	private final Router router;
	private final Proxifier proxifier;

	public DefaultStatus(HttpServletResponse response, Result result, Configuration config,
			Proxifier proxifier, Router router) {
		this.response = response;
		this.result = result;
		this.config = config;
		this.proxifier = proxifier;
		this.router = router;
	}

	public void notFound() {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		result.use(Results.nothing());
	}

	public void header(String key, String value) {
		response.addHeader(key, value);
	}

	public void created() {
		response.setStatus(HttpServletResponse.SC_CREATED);
		result.use(Results.nothing());
	}

	public void created(String location) {
		header("Location", fixLocation(location));
		created();
	}

	public void ok() {
		response.setStatus(HttpServletResponse.SC_OK);
		result.use(Results.nothing());
	}

	public void conflict() {
		response.setStatus(HttpServletResponse.SC_CONFLICT);
		result.use(Results.nothing());
	}

	public void methodNotAllowed(EnumSet<HttpMethod> allowedMethods) {
		header("Allow", allowedMethods.toString().replaceAll("\\[|\\]", ""));

		response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		result.use(Results.nothing());
	}

	public void movedPermanentlyTo(String location) {
		this.response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		header("Location", fixLocation(location));

		this.response.addIntHeader("Content-length", 0);
		this.response.addDateHeader("Date", System.currentTimeMillis());
	}

	private String fixLocation(String location) {
		if (location.startsWith("/")) {
			return config.getApplicationPath() + location;
		}
		return location;
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

	public void unsupportedMediaType(String message) {
		try {
			response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, message);
			result.use(Results.nothing());
		} catch (IOException e) {
			throw new ResultException(e);
		}
	}

	public void badRequest(String message) {
		try {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
			result.use(Results.nothing());
		} catch (IOException e) {
			throw new ResultException(e);
		}
	}

}
