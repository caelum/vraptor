
package br.com.caelum.vraptor.ioc.pico;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Prepares all classes annotated with @Convert to be used as converters.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
@ApplicationScoped
public class ConverterRegistrar implements Registrar {

    private final Logger logger = LoggerFactory.getLogger(ConverterRegistrar.class);
    private final Converters converters;

    public ConverterRegistrar(Converters converters) {
        this.converters = converters;
    }

    @SuppressWarnings({"unchecked"})
    public void registerFrom(Scanner scanner) {
        logger.info("Registering all custom converters annotated with @Convert");
        Collection<Class<?>> converterTypes = scanner.getTypesWithAnnotation(Convert.class);

        for (Class<?> converterType : converterTypes) {
            if (!Converter.class.isAssignableFrom(converterType)) {
                throw new VRaptorException("converter " + converterType + "does not implement Converter");
            }
            logger.debug("found converter: " + converterType);
            converters.register((Class<? extends Converter<?>>) converterType);
        }
    }
}
