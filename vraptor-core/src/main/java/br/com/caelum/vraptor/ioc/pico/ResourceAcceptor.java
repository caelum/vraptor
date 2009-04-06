package br.com.caelum.vraptor.ioc.pico;

import java.lang.annotation.Annotation;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.Stereotype;
import br.com.caelum.vraptor.resource.DefaultResource;
import br.com.caelum.vraptor.resource.Resource;

public class ResourceAcceptor implements Acceptor {

    private static final Logger logger = LoggerFactory.getLogger(ResourceAcceptor.class);
    private final List<Resource> results;

    public ResourceAcceptor(List<Resource> results) {
        this.results = results;
    }

    public void analyze(Class<?> type) {
        for (Annotation annotation : type.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(Stereotype.class)) {
                logger.debug("Found resource for " + type);
                results.add(new DefaultResource(type));
            }
        }
    }

}
