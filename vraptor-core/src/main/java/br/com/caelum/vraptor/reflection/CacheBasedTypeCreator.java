
package br.com.caelum.vraptor.reflection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.http.AbstractTypeCreator;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A type creator which caches its classes so it doesnt keep generating classes
 * between every call.
 *
 * @author Guilherme Silveira
 */

@ApplicationScoped
public class CacheBasedTypeCreator extends AbstractTypeCreator {

    private static final Logger logger = LoggerFactory.getLogger(CacheBasedTypeCreator.class);
    private final Map<Method, Class<?>> cache = new HashMap<Method, Class<?>>();
    private final TypeCreator creator;

    public CacheBasedTypeCreator(TypeCreator creator, ParameterNameProvider provider) {
    	super(provider);
        this.creator = creator;
    }

    public Class<?> typeFor(ResourceMethod method) {
        if (!cache.containsKey(method.getMethod())) {
            cache.put(method.getMethod(), creator.typeFor(method));
            logger.debug("cached generic type for method " + method);
        }
        return cache.get(method.getMethod());
    }

}
