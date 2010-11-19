package br.com.caelum.vraptor.jersey;

import java.lang.annotation.Annotation;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.http.route.RoutesParser;
import br.com.caelum.vraptor.ioc.StereotypeHandler;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.vraptor2.VRaptor2ComponentHandler;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.WebApplicationFactory;

public class JerseyStereotypeHandler implements StereotypeHandler{

	private static final Logger logger = LoggerFactory.getLogger(VRaptor2ComponentHandler.class);

	private final ComponentRegistry components;
	private final Router router;

	private final RoutesParser parser;

	private ResourceConfig config;

	private WebApplication application;

	public JerseyStereotypeHandler(ResourceConfig config, ComponentRegistry components, Router router, RoutesParser parser) {
		this.config = config;
		this.application = WebApplicationFactory.createWebApplication();
		this.application.initiate(config);
		this.components = components;
		this.router = router;
		this.parser = parser;
	}

	public void handle(Class<?> type) {
		logger.debug("registering vraptor2 component {}", type);
		components.register(type, type);
		List<Route> rules = parser.rulesFor(new DefaultResourceClass(type));
		for (Route route : rules) {
			router.add(route);
		}
	}

	public Class<? extends Annotation> stereotype() {
		return Component.class;
	}

}
