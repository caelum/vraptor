package br.com.caelum.vraptor.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Fabio Kung
 */
public class VRaptorRequest {
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public VRaptorRequest(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }
}
