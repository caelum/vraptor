package br.com.caelum.vraptor.core;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Fabio Kung
 */
public class VRaptorRequest {
    private final ServletContext servletContext;

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    public VRaptorRequest(ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) {
        this.servletContext = servletContext;
        this.request = request;
        this.response = response;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}
