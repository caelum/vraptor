package br.com.caelum.vraptor.view;

import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor.resource.ResourceMethod;

public class SessionFlashScope implements FlashScope {

	private final HttpSession session;

	private static final String KEY_START = "vraptor_flash_parameters_for_";

	public SessionFlashScope(HttpSession session) {
		this.session = session;
	}

	public Object[] consumeParameters(ResourceMethod method) {
		Object[] args = (Object[]) session.getAttribute(nameFor(method));
		if (args != null) {
			session.removeAttribute(nameFor(method));
		}
		return args;
	}

	private String nameFor(ResourceMethod method) {
		return KEY_START + method.getMethod();
	}

	public void includeParameters(ResourceMethod method, Object[] args) {
		session.setAttribute(nameFor(method), args);
	}

}
