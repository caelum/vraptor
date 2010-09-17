package br.com.caelum.vraptor.ioc.guice;

import javax.servlet.ServletContext;

import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.spring.VRaptorRequestHolder;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scope;

public class GuiceProvider implements ContainerProvider {

	private static final Scope REQUEST = new RequestCustomScope();
	private static final Scope SESSION = new SessionCustomScope();
	private final class GuiceContainer implements Container {
		public <T> T instanceFor(Class<T> type) {
			return injector.getInstance(type);
		}

		public <T> boolean canProvide(Class<T> type) {
			return instanceFor(type) != null;
		}
	}

	private Injector injector;
	private GuiceContainer container;

	public <T> T provideForRequest(RequestInfo request, Execution<T> execution) {
		VRaptorRequestHolder.setRequestForCurrentThread(request);
		try {
			return execution.insideRequest(container);
		} finally {
			VRaptorRequestHolder.resetRequestForCurrentThread();
		}
	}

	public void start(ServletContext context) {
		container = new GuiceContainer();
		injector = Guice.createInjector(new VRaptorAbstractModule(REQUEST, SESSION, context, container));
	}

	public void stop() {
	}

}
