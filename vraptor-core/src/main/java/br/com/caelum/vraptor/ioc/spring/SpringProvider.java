package br.com.caelum.vraptor.ioc.spring;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;

import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ComponentRegistrar;
import br.com.caelum.vraptor.ioc.ContainerProvider;

/**
 * @author Fabio Kung
 */
public class SpringProvider implements ContainerProvider {
    public static final String BASE_PACKAGES_PARAMETER_NAME = "br.com.caelum.vraptor.spring.packages";

    private final RequestContextListener requestListener = new RequestContextListener();
    private SpringBasedContainer container;

    /**
     * Provides request scope support for Spring IoC Container when
     * org.springframework.web.context.request.RequestContextListener has not been called.
     */
    public <T> T provideForRequest(RequestInfo request, Execution<T> execution) {
        VRaptorRequestHolder.setRequestForCurrentThread(request);
        T result;
        if (springListenerAlreadyCalled()) {
            result = execution.insideRequest(container);
        } else {
            ServletContext context = request.getServletContext();
            HttpServletRequest webRequest = request.getRequest();
            requestListener.requestInitialized(new ServletRequestEvent(context, webRequest));
            result = execution.insideRequest(container);
            requestListener.requestDestroyed(new ServletRequestEvent(context, webRequest));
        }
        VRaptorRequestHolder.resetRequestForCurrentThread();
        return result;
    }

    public void stop() {
        container.stop();
    }

    public void start(ServletContext context) {
        String packagesParameter = context.getInitParameter(BASE_PACKAGES_PARAMETER_NAME);

        String[] packages;
        if (packagesParameter == null) {
            throw new MissingConfigurationException(BASE_PACKAGES_PARAMETER_NAME + " context-param not found in web.xml");
        } else {
            packages = packagesParameter.split(",");
        }

        container = new SpringBasedContainer(packages);
        container.start(context);

        // TODO not needed, as custom components are registered with @Component
        try {
            ComponentRegistrar registrar = container.instanceFor(ComponentRegistrar.class);
            registrar.register(container);
        } catch (NoSuchBeanDefinitionException e) {
            // there isn't any ComponentRegistrar, so custom components won't be registered.
        }
    }

    private boolean springListenerAlreadyCalled() {
        return RequestContextHolder.getRequestAttributes() != null;
    }

}
