package br.com.caelum.vraptor.vraptor2;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.StereotypeHandler;
import br.com.caelum.vraptor.resource.DefaultResourceClass;


@ApplicationScoped
public class VRaptor2ComponentHandler implements StereotypeHandler {

	private static final Logger logger = LoggerFactory.getLogger(VRaptor2ComponentHandler.class);

	private final ComponentRegistry components;
	private final Router router;

	public VRaptor2ComponentHandler(ComponentRegistry components, Router router) {
		this.components = components;
		this.router = router;
	}

	public void handle(Class<?> type) {
		logger.debug("registering vraptor2 component {}", type);
		components.register(type, type);
		router.register(new DefaultResourceClass(type));
	}

	public Class<? extends Annotation> stereotype() {
		return Component.class;
	}

}
