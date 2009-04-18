package br.com.caelum.vraptor.ioc.pico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.RegisterContainer;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.SessionScoped;
import br.com.caelum.vraptor.resource.ResourceRegistry;

public class PicoContainersProvider implements RegisterContainer {

    private static final String CONTAINER_SESSION_KEY = PicoContainersProvider.class.getName() + ".session";

    private static final Logger logger = LoggerFactory.getLogger(PicoContainersProvider.class);

    private List<Class<?>> applicationScoped = new ArrayList<Class<?>>();
    private List<Class<?>> sessionScoped = new ArrayList<Class<?>>();
    private List<Class<?>> requestScoped = new ArrayList<Class<?>>();
    private final MutablePicoContainer container;

    public PicoContainersProvider(MutablePicoContainer container) {
        this.container = container;
    }

    public void register(Class<?> type) {
        if (type.isAnnotationPresent(ApplicationScoped.class)) {
            this.applicationScoped.add(type);
            this.container.addComponent(type);
        } else if (type.isAnnotationPresent(SessionScoped.class)) {
            this.sessionScoped.add(type);
        } else {
            this.requestScoped.add(type);
        }
    }

    public Container provide(VRaptorRequest request) {
        if (logger.isDebugEnabled()) {
            logger.debug("Request components are " + Arrays.asList(requestScoped));
            logger.debug("Session components are " + sessionScoped);
        }
        HttpSession session = request.getRequest().getSession();
        MutablePicoContainer sessionContainer = (MutablePicoContainer) session.getAttribute(CONTAINER_SESSION_KEY);
        MutablePicoContainer container = new PicoBuilder(this.container).withCaching().build();
        // TODO guarantee request component registered order on tests!!!
        for (Class<?> componentType : requestScoped) {
            container.addComponent(componentType);
        }
        for (Class<?> componentType : childComponents) {
            container.addComponent(componentType);
        }
        for (Class<? extends Interceptor> type : instanceFor(InterceptorRegistry.class).all()) {
            container.addComponent(type);
        }
        container.addComponent(request.getRequest().getSession());
        container.addComponent(request).addComponent(request.getRequest()).addComponent(request.getResponse());
        // cache(CachedConverters.class, Converters.class);
        PicoBasedContainer baseContainer = new PicoBasedContainer(container, request,
                instanceFor(ResourceRegistry.class));
        return baseContainer;
    }

}
