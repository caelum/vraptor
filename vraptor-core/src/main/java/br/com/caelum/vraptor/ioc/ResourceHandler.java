/**
 * 
 */
package br.com.caelum.vraptor.ioc;

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.ioc.pico.ResourceRegistrar;
import br.com.caelum.vraptor.resource.DefaultResourceClass;

@ApplicationScoped
@org.springframework.stereotype.Component("stereotypeHandler")
public class ResourceHandler implements StereotypeHandler {
	private final Logger logger = LoggerFactory.getLogger(ResourceRegistrar.class);
	private final Router router;
	
	public ResourceHandler(Router router) {
		this.router = router;
	}

	public void handle(Class<?> annotatedType) {
		logger.debug("Found resource: " + annotatedType);
		router.register(new DefaultResourceClass(annotatedType));
	}

	public Class<? extends Annotation> stereotype() {
		return Resource.class;
	}
}