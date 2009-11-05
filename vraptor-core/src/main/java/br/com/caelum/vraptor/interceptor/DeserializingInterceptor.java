package br.com.caelum.vraptor.interceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.deserialization.Deserializers;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.HttpResult;

/**
 * Important: this interceptor must precede the {@link ParametersInstantiatorInterceptor}
 *
 * @author Lucas Cavalcanti, Rafael Ferreira
 */
public class DeserializingInterceptor implements Interceptor {
	private static final int UNSUPPORTED_MEDIA_TYPE = 415;
	private final HttpServletRequest request;
	private final HttpResult result;
	private final Deserializers deserializers;

	public DeserializingInterceptor(HttpServletRequest servletRequest, HttpResult result, Deserializers deserializers) {
		this.request = servletRequest;
		this.result = result;
		this.deserializers = deserializers;
	}

	public boolean accepts(ResourceMethod method) {
		return method.containsAnnotation(Consumes.class);
	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
		Consumes consumesAnnotation = method.getMethod().getAnnotation(Consumes.class);
		List<String> supportedMimeTypes =  Arrays.asList(consumesAnnotation.value());

		String contentType = request.getContentType();
		if (!supportedMimeTypes.contains(contentType)) {
			String errorMessage = String.format("Request with media type [%s]. Expecting one of %s.",
							contentType, supportedMimeTypes);
			result.sendError(UNSUPPORTED_MEDIA_TYPE, errorMessage);
			return;
		}

		try {
			Object[] parameters = deserializers.deserialize(request.getInputStream(), contentType, method);
		} catch (IOException e) {
			throw new InterceptionException(e);
		}

	}

}
