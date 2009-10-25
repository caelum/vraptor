package br.com.caelum.vraptor.interceptor;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.http.MutableResponse.RedirectListener;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Interceptor that handles flash scope.
 * @author Lucas Cavalcanti
 * @author Adriano Almeida
 */
public class FlashInterceptor implements Interceptor {

	final static String FLASH_INCLUDED_PARAMETERS = "br.com.caelum.vraptor.flash.parameters";
	private static final Logger LOGGER = LoggerFactory.getLogger(FlashInterceptor.class);

	private final HttpSession session;
	private final Result result;
	private final MutableResponse response;

	public FlashInterceptor(HttpSession session, Result result, MutableResponse response) {
		this.session = session;
		this.result = result;
		this.response = response;
	}

	public boolean accepts(ResourceMethod method) {
		return true;
	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
			throws InterceptionException {
		Map<String, Object> parameters = (Map<String, Object>) session.getAttribute(FLASH_INCLUDED_PARAMETERS);
		if (parameters != null) {
			session.removeAttribute(FLASH_INCLUDED_PARAMETERS);
			for (Entry<String, Object> parameter : parameters.entrySet()) {
				result.include(parameter.getKey(), parameter.getValue());
			}
		}
		response.addRedirectListener(new RedirectListener() {
			public void beforeRedirect() {
				try {
					session.setAttribute(FLASH_INCLUDED_PARAMETERS, result.included());
				} catch (IllegalStateException e) {
					LOGGER.warn("HTTP Session was invalidated. It is not possible to include " +
							"Result parameters on Flash Scope", e);
				}
			}
		});
		stack.next(method, resourceInstance);

	}


}
