package br.com.caelum.vraptor.ioc.spring;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.FactoryBean;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Provides the current javax.servlet.http.HttpSession object, provided that Spring has registered it for the
 * current Thread.
 *
 * @author Fabio Kung
 * @see org.springframework.web.context.request.RequestContextHolder
 */
@ApplicationScoped
class HttpSessionProvider implements FactoryBean {

    public Object getObject() throws Exception {
        return VRaptorRequestHolder.currentRequest().getRequest().getSession();
    }

    public Class getObjectType() {
        return HttpSession.class;
    }

    public boolean isSingleton() {
        return false;
    }
}