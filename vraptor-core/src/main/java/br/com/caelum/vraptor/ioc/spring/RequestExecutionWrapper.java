package br.com.caelum.vraptor.ioc.spring;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequestEvent;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextListener;

import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.VRaptorRequest;

/**
 * @author Fabio Kung
 */
class RequestExecutionWrapper implements RequestExecution {
    private RequestExecution execution;
    private RequestContextListener requestListener;
    private VRaptorRequest request;

    public RequestExecutionWrapper(RequestExecution execution, VRaptorRequest request) {
        this.execution = execution;
        requestListener = new RequestContextListener();
        this.request = request;
    }

    public void execute() throws IOException {
        ServletContext servletContext = request.getServletContext();
        HttpServletRequest servletRequest = request.getRequest();

        requestListener.requestInitialized(new ServletRequestEvent(servletContext, servletRequest));
        execution.execute();
        requestListener.requestDestroyed(new ServletRequestEvent(servletContext, servletRequest));
    }
}
