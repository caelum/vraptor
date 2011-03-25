package br.com.caelum.vraptor.flex;
import br.com.caelum.vraptor.ioc.Container;
import flex.messaging.FactoryInstance;
import flex.messaging.FlexFactory;
import flex.messaging.config.ConfigMap;

public class VRaptorFactoryInstance extends FactoryInstance {

	private final Container container;

	public VRaptorFactoryInstance(FlexFactory factory, String id,
			ConfigMap properties, Container container) {
		super(factory, id, properties);
		this.container = container;
	}
	
	@Override
	public Object lookup() {
		Class<?> type;
		try {
			type = Class.forName(getSource());
			return container.instanceFor(type);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Source destination does not match a vraptor controller", e);
		}
	}
}
