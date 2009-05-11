package br.com.caelum.vraptor.ioc.spring;

import org.springframework.beans.factory.FactoryBean;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * @author Fabio Kung
 */
@ApplicationScoped
class VRaptorRequestProvider implements FactoryBean {

    public Object getObject() throws Exception {
        return VRaptorRequestHolder.currentRequest();
    }

    public Class getObjectType() {
        return RequestInfo.class;
    }

    public boolean isSingleton() {
        return false;
    }
}
