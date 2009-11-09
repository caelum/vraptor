package br.com.caelum.vraptor.view;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.config.Configuration;
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
	private final Configuration config;

	public DefaultStatus(HttpServletResponse response, Result result,  Configuration config) {
		this.response = response;
		this.result = result;
		this.config = config;
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
		header("Location", config.getApplicationPath() + location);
		created();
	}
	
	public void ok() {
		response.setStatus(HttpServletResponse.SC_OK);
		result.use(Results.nothing());
	}

}
