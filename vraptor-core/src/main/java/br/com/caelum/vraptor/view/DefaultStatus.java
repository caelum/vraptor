package br.com.caelum.vraptor.view;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.Routes;
import br.com.caelum.vraptor.ioc.Component;

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

	public DefaultStatus(HttpServletResponse response, Result result, Routes routes) {
		this.response = response;
		this.result = result;
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
		header("Location", location);
		created();
	}

}
