package br.com.caelum.vraptor.reflection;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A type creator which caches its classes so it doesnt keep generating classes
 * between every call.
 * 
 * @author Guilherme Silveira
 */
public class CacheBasedTypeCreator implements TypeCreator {

    private static final Logger logger = LoggerFactory.getLogger(CacheBasedTypeCreator.class);
    private final Map<ResourceMethod, Class<?>> cache = new HashMap<ResourceMethod, Class<?>>();
    private final TypeCreator creator;

    public CacheBasedTypeCreator(TypeCreator creator) {
        this.creator = creator;
    }

    public Class<?> typeFor(ResourceMethod method) {
        if (!cache.containsKey(method)) {
            cache.put(method, creator.typeFor(method));
            logger.debug("cached generic type for method " + method);
        }
        return cache.get(method);
    }

    public String nameFor(Type paramType) {
        return creator.nameFor(paramType);
    }

}
