package br.com.caelum.vraptor.view;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.ioc.Component;

/**
 * Allows typical HEADER only results.
 * 
 * @author guilherme silveira
 * @since 3.0.3
 */
@Component
public class DefaultStatus implements Status {

	private final HttpServletResponse response;
	private final Result result;

	public DefaultStatus(HttpServletResponse response, Result result) {
		this.response = response;
		this.result = result;
	}

	public void notFound() {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		result.use(Results.nothing());
	}

	public void internalServerError(Throwable e) throws IOException {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		PrintWriter writer = response.getWriter();
		while (e != null) {
			e.printStackTrace(writer);
			e = e.getCause();
		}
		result.use(Results.nothing());
	}

	public void header(String key, String value) {
		response.addHeader(key, value);
	}

	public void created() {
		response.setStatus(HttpServletResponse.SC_CREATED);
	}

}
