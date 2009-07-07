package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.ioc.ComponentFactory;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.AbstractAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * Pico's ComponentAdapter wrapping VRaptor's ComponentFactory
 *
 * @author Sérgio Lopes
 */
@SuppressWarnings({"unchecked", "serial"})
public class PicoComponentAdapter extends AbstractAdapter {

    private static final Logger logger = LoggerFactory.getLogger(PicoComponentAdapter.class);

    private final Class<?> targetType;
    private final Class<? extends ComponentFactory> componentFactoryClass;

    public PicoComponentAdapter(Class<?> targetType, Class<? extends ComponentFactory> componentFactoryClass) {
        super(targetType, targetType);

        if (logger.isDebugEnabled())
            logger.debug("New adapter for " + componentFactoryClass.getName());

        this.targetType = targetType;
        this.componentFactoryClass = componentFactoryClass;
    }

    public Object getComponentInstance(PicoContainer pico, Type type)
            throws PicoCompositionException {

        if (logger.isDebugEnabled())
            logger.debug("Providing " + targetType.getName() + " instance via " + componentFactoryClass.getName());

        ComponentFactory<?> componentFactory = pico.getComponent(componentFactoryClass);
        return componentFactory.getInstance();
    }

    public String getDescriptor() {
        return "Adapter for " + targetType.getName();
    }

    public void verify(PicoContainer pico) throws PicoCompositionException {
    }

}
