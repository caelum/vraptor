package br.com.caelum.vraptor.vraptor2;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.picocontainer.MutablePicoContainer;

import br.com.caelum.vraptor.ioc.pico.PicoContainersProvider;

public class SessionAttributeController implements HttpSessionBindingListener{

    public void valueBound(HttpSessionBindingEvent event) {
        MutablePicoContainer container = (MutablePicoContainer) event.getSession().getAttribute(PicoContainersProvider.CONTAINER_SESSION_KEY);
        container.addComponent(event.getValue());
    }

    public void valueUnbound(HttpSessionBindingEvent event) {
        MutablePicoContainer container = (MutablePicoContainer) event.getSession().getAttribute(PicoContainersProvider.CONTAINER_SESSION_KEY);
        container.removeComponent(event.getValue());
    }

}
