package br.com.caelum.vraptor.vraptor2.outject;

import javax.servlet.http.HttpServletRequest;

/**
 * Default exporter: includes attributes in the request.
 * 
 * @author Guilehrme Silveira
 */
public class DefaultOutjecter implements Outjecter {

    private final HttpServletRequest request;

    public DefaultOutjecter(HttpServletRequest request) {
        this.request = request;
    }

    public void include(String name, Object value) {
        request.setAttribute(name, value);
    }


}
