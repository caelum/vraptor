package br.com.caelum.vraptor.ioc.pico;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.interceptor.InterceptorSequence;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * <p>Prepares all classes annotated with @Intercepts to be used as VRaptor's interceptors.</p>
 * <p>Please note that interceptor classes must implement either br.com.caelum.vraptor.interceptor.Interceptor or
 * br.com.caelum.vraptor.interceptor.InterceptorSequence</p>
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
@ApplicationScoped
public class InterceptorRegistrar implements Registrar {

    private final Logger logger = LoggerFactory.getLogger(InterceptorRegistrar.class);
    private final InterceptorRegistry registry;

    public InterceptorRegistrar(InterceptorRegistry registry) {
        this.registry = registry;
    }

    @SuppressWarnings({"unchecked"})
    public void registerFrom(Scanner scanner) {
        logger.info("Registering all interceptors annotated with @Intercepts");
        Collection<Class<?>> interceptorTypes = scanner.getTypesWithAnnotation(Intercepts.class);

        for (Class<?> interceptorType : interceptorTypes) {
            if (Interceptor.class.isAssignableFrom(interceptorType)) {
                logger.debug("Found interceptor for " + interceptorType);
                registry.register((Class<? extends Interceptor>) interceptorType);
            } else if (InterceptorSequence.class.isAssignableFrom(interceptorType)) {
                logger.debug("Found interceptor sequence for " + interceptorType);
                registry.register(parseSequence((Class<? extends InterceptorSequence>) interceptorType));
            } else {
                throw new VRaptorException("Annotation " + Intercepts.class + " found in " + interceptorType
                        + ", but it is neither an Interceptor nor an InterceptorSequence.");
            }
        }

    }

    private static Class<? extends Interceptor>[] parseSequence(Class<? extends InterceptorSequence> type) {
        try {
            InterceptorSequence sequence = type.getConstructor().newInstance();
            return sequence.getSequence();
        } catch (Exception e) {
            throw new VRaptorException("Problem ocurred while instantiating an interceptor sequence", e);
        }
    }

}