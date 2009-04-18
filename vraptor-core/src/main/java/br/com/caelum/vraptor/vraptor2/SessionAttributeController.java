package br.com.caelum.vraptor.vraptor2;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.picocontainer.MutablePicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.pico.PicoContainersProvider;

public class SessionAttributeController implements HttpSessionBindingListener {

    private static final Logger logger = LoggerFactory.getLogger(SessionAttributeController.class);

    public void valueBound(HttpSessionBindingEvent event) {
        MutablePicoContainer container = (MutablePicoContainer) event.getSession().getAttribute(
                PicoContainersProvider.CONTAINER_SESSION_KEY);
        if (container != null) {
            container.addComponent(event.getValue());
        } else {
            logger.debug("Ignoring attribute bounding because there is no container for attribute " + event.getName());
        }
    }

    public void valueUnbound(HttpSessionBindingEvent event) {
        MutablePicoContainer container = (MutablePicoContainer) event.getSession().getAttribute(
                PicoContainersProvider.CONTAINER_SESSION_KEY);
        if (container != null) {
            container.removeComponent(event.getValue());
        } else {
            logger.debug("Ignoring attribute removal because there is no container for attribute " + event.getName());
        }
    }

}
