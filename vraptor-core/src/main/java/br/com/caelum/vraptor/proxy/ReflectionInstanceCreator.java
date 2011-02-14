package br.com.caelum.vraptor.proxy;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Java Reflection implementation for {@link InstanceCreator}.
 * 
 * @author Fabio Kung
 */
@ApplicationScoped
public class ReflectionInstanceCreator
    implements InstanceCreator {

    private final Logger logger = LoggerFactory.getLogger(ReflectionInstanceCreator.class);

    public <T> T instanceFor(Class<T> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?> defaultConstructor = findDefaultConstructor(constructors);

        if (defaultConstructor != null) {
            logger.debug("Default constructor found in {} ", clazz);
            return useDefaultConstructor(clazz);
        } else {
            logger.info(String.format("No default constructor found for %s. Trying to create the proxy with other "
                    + "constructors (there are %d).", clazz, constructors.length));
            return tryAllConstructors(clazz, constructors);
        }
    }

    private <T> T useDefaultConstructor(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new ProxyCreationException(e);
        }
    }

    private <T> T tryAllConstructors(Class<T> type, Constructor<?>[] constructors) {
        List<Throwable> problems = new ArrayList<Throwable>();

        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] parameterValues = proxyParameters(parameterTypes);

            if (logger.isDebugEnabled()) {
                Object[] params = { Arrays.toString(parameterTypes), Arrays.toString(parameterValues) };
                logger.debug("trying constructor with following parameters types: {} values are going to be: {}", params);
            }

            try {
                Object newInstance = constructor.newInstance(parameterValues);
                return type.cast(newInstance);

            } catch (Throwable e) {
                logger.debug("Problem while calling constructor with parameters {}. Trying next.", constructor.getParameterTypes(), e);
                problems.add(e);
                continue; // try next constructor
            }
        }

        String message = String.format("Tried to instantiate type: %s %d times, but none of the attempts worked. "
                + "The exceptions are: %s", type, constructors.length, problems);
        throw new ProxyCreationException(message);
    }

    /**
     * By now, we are always passing null as constructor parameters. If needed, we can create proxies for each
     * parameter, changing this method.
     * 
     * @param parameterTypes of the constructor to be called, in order.
     * @return parameter instances for the given types.
     */
    private Object[] proxyParameters(Class<?>[] parameterTypes) {
        return new Object[parameterTypes.length];
    }

    /**
     * @param constructors from the type to be proxified
     * @return null when there isn't a default (null) constructor
     */
    private Constructor<?> findDefaultConstructor(Constructor<?>[] constructors) {
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterTypes().length == 0) {
                return constructor;
            }
        }
        return null;
    }
}
