package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.core.VRaptorRequest;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * Provides the current javax.servlet.http.HttpServletRequest object, provided that Spring has registered it for the
 * current Thread.
 *
 * @author Fabio Kung
 * @see org.springframework.web.context.request.RequestContextHolder
 */
@Component("vraptorHttpServletRequestFactoryBean")
public class HttpServletRequestProvider implements FactoryBean {
    private final HttpServletRequest request;

    public HttpServletRequestProvider(VRaptorRequest request) {
        this.request = request.getRequest();
    }

    public Object getObject() throws Exception {
        return request;
    }

    public Class getObjectType() {
        return HttpServletRequest.class;
    }

    public boolean isSingleton() {
        return false;
    }
}
