package br.com.caelum.vraptor.flex.graniteds;

import org.granite.config.flex.Destination;
import org.granite.messaging.service.ServiceException;
import org.granite.messaging.service.ServiceInvoker;

public class VRaptorServiceInvoker extends ServiceInvoker<VraptorServiceFactory> {
	private static final long serialVersionUID = 1L;

	protected VRaptorServiceInvoker(Destination destination, VraptorServiceFactory factory, Object instance) throws ServiceException {
		super(destination, factory);
        this.invokee = instance;
	}


}
