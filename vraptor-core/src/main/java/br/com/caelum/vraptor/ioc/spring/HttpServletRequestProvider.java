
package br.com.caelum.vraptor.ioc.spring;

import org.springframework.beans.factory.FactoryBean;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Provides the current javax.servlet.http.HttpServletRequest object, provided that Spring has registered it for the
 * current Thread.
 *
 * @author Fabio Kung
 * @see org.springframework.web.context.request.RequestContextHolder
 */
@ApplicationScoped
class HttpServletRequestProvider implements FactoryBean {

    public Object getObject() throws Exception {
        return VRaptorRequestHolder.currentRequest().getRequest();
    }

    public Class<?> getObjectType() {
        return MutableRequest.class;
    }

    public boolean isSingleton() {
        return false;
    }
}
