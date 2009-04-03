package br.com.caelum.vraptor.core;

import java.util.Iterator;
import java.util.LinkedList;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.converter.PrimitiveIntConverter;
import br.com.caelum.vraptor.ioc.Container;

public class DefaultConverters implements Converters {

    private LinkedList<Class<? extends Converter<?>>> types;

    public static final Class<? extends Converter<?>>[] DEFAULTS = new Class[] { PrimitiveIntConverter.class,
            LongConverter.class };

    private final Container container;

    public DefaultConverters(Container container) {
        this.container = container;
        this.types = new LinkedList<Class<? extends Converter<?>>>();
        for (Class<? extends Converter<?>> type : DEFAULTS) {
            register(type);
        }
    }

    protected void register(Class<? extends Converter<?>> converterType) {
        if (!converterType.isAnnotationPresent(Convert.class)) {
            // TODO is this the correct one?
            throw new IllegalArgumentException("The converter type " + converterType.getName()
                    + " should have the Convert annotation");
        }
        types.add(converterType);
    }

    public Converter to(Class type) {
        for (Iterator iterator = types.iterator(); iterator.hasNext();) {
            Class<? extends Converter> converterType = (Class<? extends Converter>) iterator.next();
            Class boundType = converterType.getAnnotation(Convert.class).value();
            if (boundType.isAssignableFrom(type)) {
                container.register(converterType);
                return container.instanceFor(converterType);
            }
        }
        // TODO improve
        throw new IllegalArgumentException("Unable to find converter for " + type.getName());
    }

}
