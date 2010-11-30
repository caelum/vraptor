package br.com.caelum.vraptor.jersey;

import java.lang.annotation.Annotation;

import br.com.caelum.vraptor.ioc.Container;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

public class MyPorra implements InjectableProvider {

	private final HttpContext context;

	public MyPorra(HttpContext context) {
		this.context = context;
	}

	public Injectable getInjectable(ComponentContext context, Annotation annotation,
			Object ha) {
		final Class type = null;
		if(containerNotReady()) {
			return null;
		}
		return new Injectable() {
			public Object getValue() {
				Container container = getContainer();
				return (IoCComponentProvider) (container.canProvide(type) ? container
					.instanceFor(type) : null);
			}
		};
	}

	public ComponentScope getScope() {
		return ComponentScope.PerRequest;
	}

	private boolean containerNotReady() {
		try {
			context.getProperties();
		} catch (java.lang.IllegalStateException e) {
			return true;
		}
		return false;
	}

	private Container getContainer() {
		return (Container) context.getProperties().get(DefaultJersey.CONTAINER);
	}

}
