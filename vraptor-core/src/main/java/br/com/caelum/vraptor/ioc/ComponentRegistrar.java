package br.com.caelum.vraptor.ioc;

import br.com.caelum.vraptor.ComponentRegistry;

/**
 * If a ComponentRegistrar is available (annotated with @Component), it is used by VRaptor to
 * register additional component implementations.
 *
 * @author Fabio Kung
 */
public interface ComponentRegistrar {
    /**
     * Use this method to add custom components to be used by VRaptor.
     * ANY bundled component can be overriden.
     *
     * @param registry
     */
    void register(ComponentRegistry registry);
}
