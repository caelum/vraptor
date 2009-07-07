package br.com.caelum.vraptor.ioc.pico;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.interceptor.InterceptorSequence;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
public class InterceptorRegistrar implements Registrar {

	private static final Logger logger = LoggerFactory.getLogger(InterceptorRegistrar.class);
	private final InterceptorRegistry registry;

	public InterceptorRegistrar(InterceptorRegistry registry) {
		this.registry = registry;
	}

	@SuppressWarnings("unchecked")
	public void analyze(Class<?> type) {

		if (type.isAnnotationPresent(Intercepts.class)) {
			if (Interceptor.class.isAssignableFrom(type)) {
				logger.debug("Found interceptor for " + type);
				List<Class<? extends Interceptor>> list = new ArrayList<Class<? extends Interceptor>>();
				list.add((Class<? extends Interceptor>) type);
				registry.register(list);
			} else if (InterceptorSequence.class.isAssignableFrom(type)) {
				logger.debug("Found interceptor sequence for " + type);
				registry.register(parseSequence(type));
			} else {
				logger.error("Annotation " + Intercepts.class + " found in " + type
						+ " but this is neither an Interceptor nor an InterceptorSequence. Ignoring");
			}
		}
	}

	private static List<Class<? extends Interceptor>> parseSequence(Class<?> type) {
		try {
			InterceptorSequence sequence = InterceptorSequence.class.cast(type.getConstructor().newInstance());
			return Arrays.asList(sequence.getSequence());
		} catch (InvocationTargetException e) {
			logger.error("Problem ocurred while instantiating an interceptor sequence", e.getCause());
		} catch (Exception e) {
			logger.error("Problem ocurred while instantiating an interceptor sequence", e);
		}
		return Collections.emptyList();
	}

}