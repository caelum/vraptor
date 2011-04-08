package br.com.caelum.vraptor.flex.graniteds;

import org.granite.config.flex.Destination;
import org.granite.context.GraniteContext;
import org.granite.messaging.service.ServiceException;
import org.granite.messaging.service.ServiceInvoker;
import org.granite.messaging.service.SimpleServiceFactory;
import org.granite.util.XMap;

import br.com.caelum.vraptor.flex.VRaptorLookup;
import flex.messaging.messages.RemotingMessage;

public class VraptorServiceFactory extends SimpleServiceFactory {

	private static final long serialVersionUID = 1L;

	@Override
	public void configure(XMap properties) throws ServiceException {
		super.configure(properties);
	}

	@Override
	public ServiceInvoker<?> getServiceInstance(RemotingMessage request) throws ServiceException {
		String messageType = request.getClass().getName();
		String destinationId = request.getDestination();

		GraniteContext context = GraniteContext.getCurrentInstance();
		Destination destination = context.getServicesConfig().findDestinationById(messageType, destinationId);
		if (destination == null)
			throw new ServiceException("No matching destination: " + destinationId);

		String service = destination.getProperties().get("source");

		Object instance = new VRaptorLookup().lookup(service);

		return createServiceInvoker(destination, this, instance);
	}

	private ServiceInvoker<?> createServiceInvoker(Destination destination, VraptorServiceFactory factory,
			Object instance) {
		return new VRaptorServiceInvoker(destination, this, instance);
	}

}
