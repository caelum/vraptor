package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.core.VRaptorRequest;
import org.springframework.beans.factory.FactoryBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Provides the current javax.servlet.http.HttpSession object, provided that Spring has registered it for the
 * current Thread.
 *
 * @author Fabio Kung
 * @see org.springframework.web.context.request.RequestContextHolder
 */
public class HttpSessionProvider implements FactoryBean {
    private final HttpServletRequest request;

    public HttpSessionProvider(VRaptorRequest request) {
        this.request = request.getRequest();
    }

    public Object getObject() throws Exception {
        return request.getSession();
    }

    public Class getObjectType() {
        return HttpSession.class;
    }

    public boolean isSingleton() {
        return false;
    }
}