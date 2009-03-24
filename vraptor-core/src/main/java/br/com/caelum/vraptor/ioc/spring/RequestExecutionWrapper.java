package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.VRaptorRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletContext;
import java.io.IOException;

import org.springframework.web.context.request.RequestContextListener;

/**
 * @author Fabio Kung
 */
class RequestExecutionWrapper implements RequestExecution {
    private RequestExecution execution;
    private RequestContextListener requestListener;

    public RequestExecutionWrapper(RequestExecution execution) {
        this.execution = execution;
        requestListener = new RequestContextListener();
    }

    public void execute(VRaptorRequest request) throws IOException {
        ServletContext servletContext = request.getServletContext();
        HttpServletRequest servletRequest = request.getRequest();

        requestListener.requestInitialized(new ServletRequestEvent(servletContext, servletRequest));
        execution.execute(request);
        requestListener.requestDestroyed(new ServletRequestEvent(servletContext, servletRequest));
    }
}
