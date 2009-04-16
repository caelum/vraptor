package br.com.caelum.vraptor.ioc.spring;

import org.springframework.beans.factory.FactoryBean;

import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.core.VRaptorRequest;

/**
 * Provides the current javax.servlet.http.HttpServletResponse object, provided that Spring has registered it for the
 * current Thread.
 *
 * @author Fabio Kung
 * @see org.springframework.web.context.request.ServletWebRequest
 */
public class HttpServletResponseProvider implements FactoryBean {
    private VRaptorRequest request;

    public HttpServletResponseProvider(VRaptorRequest request) {
        this.request = request;
    }

    public Object getObject() throws Exception {
        return request.getResponse();
    }

    public Class getObjectType() {
        return HttpServletResponse.class;
    }

    public boolean isSingleton() {
        return false;
    }
}
