package br.com.caelum.vraptor.util.hibernate.extra;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.isEmpty;
import static java.util.Arrays.asList;

import java.io.Serializable;
import java.lang.annotation.Annotation;

import javax.servlet.http.HttpServletRequest;

import net.vidageek.mirror.dsl.Mirror;

import org.hibernate.Session;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Lazy;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Interceptor that loads given entity from the database.
 *
 * @author Lucas Cavalcanti
 * @author Cecilia Fernandes
 * @since 3.3.2
 *
 */
@Intercepts(before=ParametersInstantiatorInterceptor.class)
@Lazy
public class ParameterLoaderInterceptor implements Interceptor {

	private final Session session;
	private final HttpServletRequest request;
	private final ParameterNameProvider provider;
	private final Result result;
	private final Converters converters;
	private final Localization localization;

	public ParameterLoaderInterceptor(Session session, HttpServletRequest request, ParameterNameProvider provider,
			Result result, Converters converters, Localization localization) {
		this.session = session;
		this.request = request;
		this.provider = provider;
		this.result = result;
		this.converters = converters;
		this.localization = localization;
	}

	public boolean accepts(ResourceMethod method) {
		return any(asList(method.getMethod().getParameterAnnotations()), hasLoadAnnotation());
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

	private Object load(String name, Class type) {
		String parameter = request.getParameter(name + ".id");
		if (parameter == null) {
			return null;
		}
		Class<?> idType = new Mirror().on(type).reflect().field("id").getType();
		Converter<?> converter = converters.to(idType);

		Serializable id = (Serializable) converter.convert(parameter, type, localization.getBundle());
		return session.get(type, id);
	}

	private Predicate<Annotation[]> hasLoadAnnotation() {
		return new Predicate<Annotation[]>() {
			public boolean apply(Annotation[] param) {
				return any(asList(param), instanceOf(Load.class));
			}
		};
	}


}
