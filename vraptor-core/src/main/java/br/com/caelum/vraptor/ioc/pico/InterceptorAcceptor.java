package br.com.caelum.vraptor.ioc.pico;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.Intercepts;

public class InterceptorAcceptor implements Acceptor {

    private static final Logger logger = LoggerFactory.getLogger(InterceptorAcceptor.class);
    private final List<Class<? extends Interceptor>> interceptors;

    public InterceptorAcceptor(List<Class<? extends Interceptor>> interceptors) {
        this.interceptors = interceptors;
    }

    public void analyze(Class<?> type) {
        if (type.isAnnotationPresent(Intercepts.class)) {
            logger.debug("Found interceptor for " + type);
            interceptors.add((Class<Interceptor>) type);
        }
    }

}