package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Fabio Kung
 */
@ApplicationScoped
@Component(VRaptorApplicationContext.RESOURCES_LIST)
public class ResourcesHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesHolder.class);

    private Collection<Resource> resources = new HashSet<Resource>();

    public void add(Resource resource) {
        resources.add(resource);
    }

    public void registerAllOn(ResourceRegistry resourceRegistry) {
        LOGGER.info("registering all resources in ResourceRegistry: " + resources);
        resourceRegistry.register(resources.toArray(new Resource[resources.size()]));
    }
}
