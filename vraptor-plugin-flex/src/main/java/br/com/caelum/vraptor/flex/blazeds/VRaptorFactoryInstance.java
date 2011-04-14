package br.com.caelum.vraptor.flex.blazeds;

import br.com.caelum.vraptor.flex.VRaptorLookup;
import flex.messaging.FactoryInstance;
import flex.messaging.FlexFactory;
import flex.messaging.config.ConfigMap;

public class VRaptorFactoryInstance extends FactoryInstance {

	public VRaptorFactoryInstance(FlexFactory factory, String id, ConfigMap properties) {
		super(factory, id, properties);
	}

	@Override
	public Object lookup() {
		return new VRaptorLookup().lookup(getSource());
	}
}
