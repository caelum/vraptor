package br.com.caelum.vraptor.jersey;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * A response wrapper that does not delegate 404s. It will save its state
 * instead.
 * 
 * @author guilherme silveira
 */
class FourOFourResponseWrapper extends HttpServletResponseWrapper {
	FourOFourResponseWrapper(HttpServletRequest req,
			HttpServletResponse response) {
		super(response);
	}

	public void sendError(int sc, String msg) throws IOException {
		if (sc != 404) {
			super.sendError(sc, msg);
		}
	}

	public void sendError(int sc) throws IOException {
		if (sc != 404) {
			super.sendError(sc);
		}
	}

	public void setStatus(int sc, String sm) {
		if (sc != 404) {
			super.setStatus(sc, sm);
		}
	}

	public void setStatus(int sc) {
		if (sc != 404) {
			super.setStatus(sc);
		}
	}
}