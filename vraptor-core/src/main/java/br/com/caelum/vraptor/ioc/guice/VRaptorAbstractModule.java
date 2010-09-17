package br.com.caelum.vraptor.ioc.guice;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.ComponentFactoryIntrospector;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.ioc.SessionScoped;
import br.com.caelum.vraptor.ioc.spring.VRaptorRequestHolder;
import br.com.caelum.vraptor.serialization.Serialization;
import br.com.caelum.vraptor.validator.BeanValidator;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;

/**
 *
 * An AbstractModule that wires VRaptor components.
 *
 * @author Lucas Cavalcanti
 * @author Sergio Lopes
 *
 * @since 3.2
 *
 */
public class VRaptorAbstractModule extends AbstractModule {

	private static final Logger logger = LoggerFactory.getLogger(VRaptorAbstractModule.class);

	private final Scope request;
	private final Scope session;
	private final ServletContext context;
	private final Container container;

	public VRaptorAbstractModule(Scope request, Scope session, ServletContext context, Container container) {
		this.request = request;
		this.session = session;
		this.context = context;
		this.container = container;
	}

	@Override
	protected void configure() {
		bindScope(RequestScoped.class, request);
		bindScope(SessionScoped.class, session);

		bind(MutableRequest.class).toProvider(new Provider<MutableRequest>() {

			public MutableRequest get() {
				return VRaptorRequestHolder.currentRequest().getRequest();
			}
		}).in(request);

		bind(RequestInfo.class).toProvider(new Provider<RequestInfo>() {

			public RequestInfo get() {
				return VRaptorRequestHolder.currentRequest();
			}
		}).in(request);

		bind(HttpSession.class).toProvider(new Provider<HttpSession>() {

			public HttpSession get() {
				return VRaptorRequestHolder.currentRequest().getRequest().getSession();
			}
		}).in(request);
		bind(MutableResponse.class).toProvider(new Provider<MutableResponse>() {

			public MutableResponse get() {
				return VRaptorRequestHolder.currentRequest().getResponse();
			}
		}).in(request);
		bind(HttpServletResponse.class).to(MutableResponse.class).in(request);
		bind(HttpServletRequest.class).to(MutableRequest.class).in(request);
		bind(ServletContext.class).toInstance(context);
		bind(Container.class).toInstance(container);

		bind(new TypeLiteral<List<Serialization>>() {}).toInstance(Collections.<Serialization>emptyList());
		bind(new TypeLiteral<List<BeanValidator>>() {}).toInstance(Collections.<BeanValidator>emptyList());
		registerScope((Map) BaseComponents.getApplicationScoped());
		registerScope((Map) BaseComponents.getPrototypeScoped());
		registerScope((Map) BaseComponents.getRequestScoped(), request);

		for (Class converter : BaseComponents.getBundledConverters()) {
			bind(converter).toConstructor(converter.getDeclaredConstructors()[0]);
		}
	}

	private void registerScope(Map<Class, Class> requestScoped, Scope scope) {
		for (Entry<Class, Class> entry : requestScoped.entrySet()) {
			logger.debug("Binding {} to {}", entry.getKey(), entry.getValue());
			bind(entry.getKey()).toConstructor(entry.getValue().getDeclaredConstructors()[0]).in(scope);
		}
	}

	private void registerScope(Map<Class, Class> scope) {
		for (Entry<Class, Class> entry : scope.entrySet()) {
			bind(entry.getKey()).toConstructor(entry.getValue().getDeclaredConstructors()[0]);
			if (ComponentFactory.class.isAssignableFrom(entry.getValue())) {
				final Class<?> target = new ComponentFactoryIntrospector().targetTypeForComponentFactory(entry.getValue());
				Type adapterType = new HackedParameterizedType(ComponentFactoryProviderAdapter.class, target);
				Type factoryType = new HackedParameterizedType(ComponentFactory.class, target);
				bind(TypeLiteral.get(adapterType));
				bind(TypeLiteral.get(factoryType)).to(entry.getValue());
				bind(target).toProvider((TypeLiteral) TypeLiteral.get(adapterType));
			}
		}
	}

}
