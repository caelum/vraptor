package br.com.caelum.vraptor.ioc.pico;

import java.util.ArrayList;
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

/**
 * Provides containers, controlling all scopes and registering all different
 * components on their respective areas.
 * 
 * @author Guilherme Silveira
 */
public class PicoContainersProvider implements RegisterContainer {

    public static final String CONTAINER_SESSION_KEY = PicoContainersProvider.class.getName() + ".session";

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
        HttpSession session = request.getRequest().getSession();
        MutablePicoContainer sessionScope = (MutablePicoContainer) session.getAttribute(CONTAINER_SESSION_KEY);
        if (sessionScope == null) {
            sessionScope = createSessionContainer(session);
        }
        for (Class<?> componentType : sessionScoped) {
            container.addComponent(componentType);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Request components are " + requestScoped);
        }
        MutablePicoContainer requestScope = new PicoBuilder(sessionScope).withCaching().build();
        // TODO guarantee request component registered order on tests!!!
        for (Class<?> componentType : requestScoped) {
            requestScope.addComponent(componentType);
        }
        for (Class<? extends Interceptor> type : this.container.getComponent(InterceptorRegistry.class).all()) {
            requestScope.addComponent(type);
        }
        requestScope.addComponent(request).addComponent(request.getRequest()).addComponent(request.getResponse());
        // cache(CachedConverters.class, Converters.class);
        PicoBasedContainer baseContainer = new PicoBasedContainer(requestScope, request, this.container
                .getComponent(ResourceRegistry.class));
        return baseContainer;
    }

    private MutablePicoContainer createSessionContainer(HttpSession session) {
        if (logger.isDebugEnabled()) {
            logger.debug("Session components are " + sessionScoped);
        }
        MutablePicoContainer sessionScope = new PicoBuilder(this.container).withCaching().build();
        sessionScope.addComponent(session);
        session.setAttribute(CONTAINER_SESSION_KEY, sessionScope);
        return sessionScope;
    }

}
