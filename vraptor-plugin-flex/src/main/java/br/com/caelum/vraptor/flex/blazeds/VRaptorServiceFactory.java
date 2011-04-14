package br.com.caelum.vraptor.flex.blazeds;

import flex.messaging.FactoryInstance;
import flex.messaging.FlexFactory;
import flex.messaging.config.ConfigMap;

public class VRaptorServiceFactory implements FlexFactory {

	public void initialize(String id, ConfigMap configMap) {
	}

	public FactoryInstance createFactoryInstance(String id, ConfigMap properties) {
		VRaptorFactoryInstance instance = new VRaptorFactoryInstance(this, id, properties);
		instance.setSource(properties.getPropertyAsString(SOURCE, instance.getId()));
		return instance;
	}

	public Object lookup(FactoryInstance instance) {
		return instance.lookup();
	}

}
