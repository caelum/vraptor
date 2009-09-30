
package br.com.caelum.vraptor.ioc.pico;

import org.picocontainer.MutablePicoContainer;

import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.ioc.Container;

/**
 * A pico container based implementation of a component providing container.
 *
 * @author Guilherme Silveira
 */
public class PicoBasedContainer implements Container {

    private final MutablePicoContainer container;

    public PicoBasedContainer(MutablePicoContainer container, Router routes) {
        this.container = container;
        this.container.addComponent(this);
    }

    public <T> T instanceFor(Class<T> type) {
        return container.getComponent(type);
    }
    
    public MutablePicoContainer getContainer() {
		return container;
	}

}
