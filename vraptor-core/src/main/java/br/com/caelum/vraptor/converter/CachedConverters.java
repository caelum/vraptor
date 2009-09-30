
package br.com.caelum.vraptor.converter;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.Container;

public class CachedConverters implements Converters {

    private final Converters delegate;
    private final Map<Class<?>, Class<?>> cache = new HashMap<Class<?>, Class<?>>();

    private static final Logger logger = LoggerFactory.getLogger(CachedConverters.class);

    public CachedConverters(Converters converters) {
        this.delegate = converters;
    }

    public Converter<?> to(Class<?> type, Container container) {
        if (cache.containsKey(type)) {
            Class<?> converterType = cache.get(type);
            return (Converter<?>) container.instanceFor(converterType);
        }
        logger.debug("Caching converter " + type.getName());
        Converter<?> converter = delegate.to(type, container);
        cache.put(type, converter.getClass());
        return converter;
    }

    public void register(Class<? extends Converter<?>> converterClass) {
        throw new UnsupportedOperationException(
                "cannot add vr3 converters in cached converters container (or should we delegate?");
    }

}
