package br.com.caelum.vraptor.validator;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.view.RequestOutjectMap;

/**
 * Default implementation for {@link Outjector}.
 * It uses a magic map that mimics objects on request.
 *
 * @author Lucas Cavalcanti
 *
 */
public class DefaultOutjector implements Outjector {

	private final HttpServletRequest request;

	public DefaultOutjector(HttpServletRequest request) {
		this.request = request;
	}

	public void outjectRequestMap() {
		@SuppressWarnings("unchecked")
		Set<String> paramNames = request.getParameterMap().keySet();

		for (String paramName : paramNames) {
			paramName = extractBaseParamName(paramName);
			if (request.getAttribute(paramName) == null) {
				request.setAttribute(paramName, new RequestOutjectMap(paramName, request));
			}
		}
	}

	private String extractBaseParamName(String paramName) {
		int indexOf = paramName.indexOf('.');
		paramName = paramName.substring(0, indexOf != -1 ? indexOf : paramName.length());

		indexOf = paramName.indexOf('[');
		paramName = paramName.substring(0, indexOf != -1 ? indexOf : paramName.length());
		return paramName;
	}
}
