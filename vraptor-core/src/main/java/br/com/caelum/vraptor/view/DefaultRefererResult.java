package br.com.caelum.vraptor.view;

import static br.com.caelum.vraptor.view.Results.logic;
import static br.com.caelum.vraptor.view.Results.page;

import java.util.ArrayList;

import net.vidageek.mirror.dsl.Mirror;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;

public class DefaultRefererResult implements RefererResult {

	private final MutableRequest request;
	private final Result result;
	private final Router router;
	private final ParametersProvider provider;
	private final Localization localization;

	public DefaultRefererResult(Result result, MutableRequest request, Router router,
				ParametersProvider provider, Localization localization) {
		this.result = result;
		this.request = request;
		this.router = router;
		this.provider = provider;
		this.localization = localization;
	}

	public void forward() throws IllegalStateException {
		String referer = getReferer();

		ResourceMethod method = router.parse(referer, HttpMethod.GET, request);
		if (method == null) {
			result.use(page()).forward(referer);
		} else {
			executeMethod(method, result.use(logic()).forwardTo(method.getResource().getType()));
		}
	}

	private void executeMethod(ResourceMethod method, Object instance) {
		new Mirror().on(instance).invoke().method(method.getMethod())
			.withArgs(provider.getParametersFor(method, new ArrayList<Message>(), localization.getBundle()));
	}

	public void redirect() throws IllegalStateException {
		String referer = getReferer();
		ResourceMethod method = router.parse(referer, HttpMethod.GET, request);
		if (method == null) {
			result.use(page()).redirect(referer);
		} else {
			executeMethod(method, result.use(logic()).redirectTo(method.getResource().getType()));
		}
	}

	private String getReferer() {
		String referer = request.getHeader("Referer");
		if (referer == null) {
			throw new IllegalStateException("The Referer header was not specified");
		}

		String path = request.getContextPath();
		return referer.substring(referer.indexOf(path) + path.length());
	}

}
