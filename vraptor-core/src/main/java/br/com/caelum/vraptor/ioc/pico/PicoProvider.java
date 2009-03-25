package br.com.caelum.vraptor.ioc.pico;

import javax.servlet.ServletContext;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.http.StupidTranslator;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.resource.DefaultResourceRegistry;
import br.com.caelum.vraptor.resource.ResourceRegistry;

/**
 * Managing internal components by using pico container.
 *
 * @author Guilherme Silveira
 */
public class PicoProvider implements ContainerProvider {

    private final MutablePicoContainer container;

    public PicoProvider() {
        this.container = new PicoBuilder().withCaching().build();
        this.container.addComponent(StupidTranslator.class);
        this.container.addComponent(DefaultResourceRegistry.class);
        this.container.addComponent(DefaultDirScanner.class);
        this.container.addComponent(WebInfClassesScanner.class);
    }
    
    public <T> T instanceFor(Class<T> type) {
        return container.getComponent(type);
    }

    public void start(ServletContext context) {
        this.container.addComponent(context);
        instanceFor(ResourceLocator.class).loadAll();
        container.start();
    }

    public void stop() {
        container.stop();
    }

    public Container provide(VRaptorRequest request) {
        return new PicoBasedContainer(container, request, instanceFor(ResourceRegistry.class));
    }

}
