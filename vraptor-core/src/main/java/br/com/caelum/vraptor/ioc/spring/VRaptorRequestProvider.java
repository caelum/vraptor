package br.com.caelum.vraptor.ioc.spring;

import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import org.springframework.beans.factory.FactoryBean;

/**
 * @author Fabio Kung
 */
@ApplicationScoped
public class VRaptorRequestProvider implements FactoryBean {

    public Object getObject() throws Exception {
        return VRaptorRequestHolder.currentRequest();
    }

    public Class getObjectType() {
        return VRaptorRequest.class;
    }

    public boolean isSingleton() {
        return false;
    }
}
