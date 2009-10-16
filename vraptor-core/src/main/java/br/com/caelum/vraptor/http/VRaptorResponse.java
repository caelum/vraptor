package br.com.caelum.vraptor.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class VRaptorResponse extends HttpServletResponseWrapper implements MutableResponse {

	private final List<RedirectListener> listeners = new ArrayList<RedirectListener>();

	public VRaptorResponse(HttpServletResponse response) {
		super(response);
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		for (RedirectListener listener : listeners) {
			listener.beforeRedirect();
		}
		super.sendRedirect(location);
	}
	public void addRedirectListener(RedirectListener listener) {
		listeners.add(listener);
	}

}
