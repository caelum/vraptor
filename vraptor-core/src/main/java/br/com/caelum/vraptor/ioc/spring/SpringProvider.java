package br.com.caelum.vraptor.ioc.spring;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;

import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ContainerProvider;

/**
 * @author Fabio Kung
 */
public class SpringProvider implements ContainerProvider {
    public static final String BASE_PACKAGES_PARAMETER_NAME = "br.com.caelum.vraptor.packages";

    private final RequestContextListener requestListener = new RequestContextListener();
    private SpringBasedContainer container;

    /**
     * Provides request scope support for Spring IoC Container when
     * org.springframework.web.context.request.RequestContextListener has not been called.
     */
    public <T> T provideForRequest(RequestInfo request, Execution<T> execution) {
        VRaptorRequestHolder.setRequestForCurrentThread(request);
        T result;
        try {
	        if (springListenerAlreadyCalled()) {
	            result = execution.insideRequest(container);
	        } else {
	            ServletContext context = request.getServletContext();
	            HttpServletRequest webRequest = request.getRequest();
	            requestListener.requestInitialized(new ServletRequestEvent(context, webRequest));
	            try {
	            	result = execution.insideRequest(container);
	            } finally {
	            	requestListener.requestDestroyed(new ServletRequestEvent(context, webRequest));
	            }
	        }
        } finally {
        	VRaptorRequestHolder.resetRequestForCurrentThread();
        }
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
    }

    private boolean springListenerAlreadyCalled() {
        return RequestContextHolder.getRequestAttributes() != null;
    }

}
