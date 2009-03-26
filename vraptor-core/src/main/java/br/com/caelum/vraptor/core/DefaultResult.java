package br.com.caelum.vraptor.core;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Result;

public class DefaultResult implements Result {
    
    private final HttpServletRequest request;

    public DefaultResult(HttpServletRequest request) {
        this.request = request;
    }

    public void include(String key, Object value) {
        request.setAttribute(key, value);
    }

}
