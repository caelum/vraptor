package br.com.caelum.vraptor.flex;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.Container;
import flex.messaging.FactoryInstance;
import flex.messaging.FlexFactory;
import flex.messaging.config.ConfigMap;

@Component
@ApplicationScoped
public class VRaptorServiceFactory implements FlexFactory {

	private static Container container;

	@Inject
	@Autowired
	public VRaptorServiceFactory(Container container) {
		VRaptorServiceFactory.container = container;
	}

	public VRaptorServiceFactory() {
	}

	public void initialize(String id, ConfigMap configMap) {
	}

	public FactoryInstance createFactoryInstance(String id, ConfigMap properties) {
		VRaptorFactoryInstance instance = new VRaptorFactoryInstance(this, id, properties, container);
		instance.setSource(properties.getPropertyAsString(SOURCE, instance.getId()));
		return instance;
	}

	public Object lookup(FactoryInstance instance) {
		return instance.lookup();
	}

}
