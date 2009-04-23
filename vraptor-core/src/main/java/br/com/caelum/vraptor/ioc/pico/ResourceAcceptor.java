package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.ioc.Stereotype;
import br.com.caelum.vraptor.resource.DefaultResource;
import br.com.caelum.vraptor.resource.ResourceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;

public class ResourceAcceptor implements Acceptor {

    private static final Logger logger = LoggerFactory.getLogger(ResourceAcceptor.class);
    private final ResourceRegistry resourceRegistry;

    public ResourceAcceptor(ResourceRegistry registry) {
        resourceRegistry = registry;
    }

    public void analyze(Class<?> type) {
        for (Annotation annotation : type.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(Stereotype.class)) {
                logger.debug("Found resource for " + type);
                resourceRegistry.register(new DefaultResource(type));
            }
        }
    }

}
