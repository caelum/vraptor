package br.com.caelum.vraptor.jersey;

import br.com.caelum.vraptor.ioc.Container;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;

public class VRaptorComponentProvider implements IoCComponentProviderFactory {

	private HttpContext context;

	public IoCComponentProvider getComponentProvider(Class<?> type) {
		if(containerNotReady()) {
			return null;
		}
		Container container = getContainer();
		return (IoCComponentProvider) (container.canProvide(type) ? container
				.instanceFor(type) : null);
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

	public IoCComponentProvider getComponentProvider(ComponentContext context,
			Class<?> type) {
		if(containerNotReady()) {
			return null;
		}
		Container container = getContainer();
		return (IoCComponentProvider) (container.canProvide(type) ? container
				.instanceFor(type) : null);
	}

	public void useContext(HttpContext context) {
		this.context = context;
	}

}
