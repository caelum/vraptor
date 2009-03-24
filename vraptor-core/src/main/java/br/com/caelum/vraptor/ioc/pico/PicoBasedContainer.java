package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.core.Request;
import br.com.caelum.vraptor.core.DefaultRequest;
import br.com.caelum.vraptor.http.StupidTranslator;
import br.com.caelum.vraptor.resource.DefaultResourceRegistry;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.ioc.Container;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Managing internal components by using pico container.
 *
 * @author Guilherme Silveira
 */
public class PicoBasedContainer implements Container {

    private final MutablePicoContainer container;

    public PicoBasedContainer(ServletContext context) {
        // TODO scan classpath for @Component annotated classes
        this.container = new PicoBuilder().withCaching().build();
        this.container.addComponent(this);
        this.container.addComponent(context);
        this.container.addComponent(StupidTranslator.class);
        this.container.addComponent(DefaultResourceRegistry.class);
        this.container.addComponent(DefaultDirScanner.class);
        this.container.addComponent(WebInfClassesScanner.class);
    }

    public <T> T instanceFor(Class<T> type) {
        return container.getComponent(type);
    }

    public void start() {
        instanceFor(ResourceLocator.class).loadAll();
        container.start();
    }

    public void stop() {
        container.stop();
    }

    public Request prepare(ResourceMethod method, HttpServletRequest request, HttpServletResponse response) {
        PicoBasedRequestContainer container = new PicoBasedRequestContainer(this.container, method, request, response);
        return container.withA(DefaultRequest.class);
    }

}
