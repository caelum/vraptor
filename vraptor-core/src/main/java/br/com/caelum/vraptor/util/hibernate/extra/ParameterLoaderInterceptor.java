package br.com.caelum.vraptor.util.hibernate.extra;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.isEmpty;
import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Lazy;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

import com.google.common.collect.Iterables;

/**
 *
 *
 * @author Lucas Cavalcanti
 * @since
 *
 */
@Intercepts(before=ParametersInstantiatorInterceptor.class)
@Lazy
public class ParameterLoaderInterceptor implements Interceptor {

	private final Session session;
	private final HttpServletRequest request;
	private final ParameterNameProvider provider;
	private final Result result;

	public ParameterLoaderInterceptor(Session session, HttpServletRequest request, ParameterNameProvider provider, Result result) {
		this.session = session;
		this.request = request;
		this.provider = provider;
		this.result = result;
	}

	public boolean accepts(ResourceMethod method) {
		for (Annotation[] param : method.getMethod().getParameterAnnotations()) {
			if (any(asList(param), instanceOf(Load.class))) {
				return true;
			}
		}
		return false;
	}

	public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
			throws InterceptionException {
		Annotation[][] annotations = method.getMethod().getParameterAnnotations();

		String[] names = provider.parameterNamesFor(method.getMethod());

		Class<?>[] types = method.getMethod().getParameterTypes();

		for (int i = 0; i < names.length; i++) {
			Iterable<Load> loads = Iterables.filter(asList(annotations[i]), Load.class);
			if (!isEmpty(loads)) {
				Object loaded = load(names[i], types[i]);
				Load load = loads.iterator().next();
				if (!load.managed()) {
					session.evict(loaded);
				}
				if (loaded == null) { result.notFound(); return; }

				request.setAttribute(names[i], loaded);
			}
		}

		stack.next(method, resourceInstance);
	}

	private Object load(String name, Class<?> types) {
		String parameter = request.getParameter(name + ".id");
		if (parameter == null) {
			return null;
		}
		Long id = Long.valueOf(parameter);
		return session.get(types, id);
	}

}
