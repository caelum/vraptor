package br.com.caelum.vraptor.core;

import java.lang.reflect.Constructor;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.exception.MirrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.ResourceMethod;

class StaticInterceptorHandler implements InterceptorHandler {

	private static final Logger logger = LoggerFactory.getLogger(StaticInterceptorHandler.class);

	private final Container container;
	private final Class<? extends Interceptor> type;
	private Interceptor acceptor;

	public StaticInterceptorHandler(Container container, Class<? extends Interceptor> type) {
		this.container = container;
		this.type = type;
	}

	public void execute(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
			throws InterceptionException {
		boolean accepts;
		try {
			accepts = getAcceptor().accepts(method);
		} catch (NullPointerException e) {
			throw new InterceptionException("StaticInterceptors should not use constructor parameters inside the accepts method", e);
		}
		if (accepts) {
			Interceptor interceptor = container.instanceFor(type);
			if (interceptor == null) {
				throw new InterceptionException("Unable to instantiate interceptor for " + type.getName()
						+ ": the container returned null.");
			}
			logger.debug("Invoking interceptor {}", interceptor.getClass().getSimpleName());
			interceptor.intercept(stack, method, resourceInstance);
		} else {
			stack.next(method, resourceInstance);
		}

	}

	private Interceptor getAcceptor() {
		if (acceptor == null) {
			try {
				Constructor<?> constructor = type.getDeclaredConstructors()[0];
				int argsLength = constructor.getParameterTypes().length;
				acceptor = type.cast(new Mirror().on(type).invoke().constructor(constructor).withArgs(new Object[argsLength]));
			} catch (MirrorException e) {
				if (e.getCause() instanceof NullPointerException) {
					throw new InterceptionException("StaticInterceptors should not use constructor parameters inside the constructor", e);
				} else {
					throw new InterceptionException(e);
				}
			}
		}
		return acceptor;
	}
}
