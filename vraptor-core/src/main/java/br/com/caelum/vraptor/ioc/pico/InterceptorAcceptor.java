package br.com.caelum.vraptor.ioc.pico;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.interceptor.InterceptorSequence;

public class InterceptorAcceptor implements Acceptor {

	private static final Logger logger = LoggerFactory
			.getLogger(InterceptorAcceptor.class);
	private final List<Class<? extends Interceptor>> interceptors;

	public InterceptorAcceptor(List<Class<? extends Interceptor>> interceptors) {
		this.interceptors = interceptors;
	}

	@SuppressWarnings("unchecked")
	public void analyze(Class<?> type) {
		if (type.isAnnotationPresent(Intercepts.class)) {
			if (Interceptor.class.isAssignableFrom(type)) {
				logger.debug("Found interceptor for " + type);
				interceptors.add((Class<Interceptor>) type);
			} else if (InterceptorSequence.class.isAssignableFrom(type)) {
				logger.debug("Found interceptor sequence for " + type);
				parseSequence(type);
			} else {
				logger
						.error("Annotation "
								+ Intercepts.class
								+ " found in "
								+ type
								+ " but this is neither an Interceptor nor an InterceptorSequence. Ignoring");
			}
		}
	}

	private void parseSequence(Class<?> type) {
		try {
			InterceptorSequence sequence = InterceptorSequence.class.cast(type
					.getConstructor().newInstance());
			interceptors.addAll(Arrays.asList(sequence.getSequence()));
		} catch (InvocationTargetException e) {
			logger
					.error(
							"Problem ocurred while instantiating an interceptor sequence",
							e.getCause());
		} catch (Exception e) {
			logger
					.error(
							"Problem ocurred while instantiating an interceptor sequence",
							e);
		}
	}

}