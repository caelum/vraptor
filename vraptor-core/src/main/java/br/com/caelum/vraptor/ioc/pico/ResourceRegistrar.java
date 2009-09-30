
package br.com.caelum.vraptor.ioc.pico;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ResourceHandler;

/**
 * Prepares special components annotated with @Resource to be reachable through web requests;
 * i.e. adds them to the Router.
 *
 * @author Guilherme Silveira
 * @author Paulo Silveira
 * @author Fabio Kung
 */
@ApplicationScoped
public class ResourceRegistrar implements Registrar {
    private final Logger logger = LoggerFactory.getLogger(ResourceRegistrar.class);

	private ResourceHandler resourceHandler;

    public ResourceRegistrar(Router router) {
        resourceHandler = new ResourceHandler(router);
    }

    public void registerFrom(Scanner scanner) {
        logger.info("Registering all resources annotated with @Resource");
        Collection<Class<?>> resourceTypes = scanner.getTypesWithAnnotation(Resource.class);
        for (Class<?> resourceType : resourceTypes) {
        	resourceHandler.handle(resourceType);
        }
    }
}
