package br.com.caelum.vraptor.vraptor2;

import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.picocontainer.MutablePicoContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.pico.PicoContainersProvider;

public class SessionAttributeController implements HttpSessionAttributeListener {

    private static final Logger logger = LoggerFactory.getLogger(SessionAttributeController.class);

    public void attributeAdded(HttpSessionBindingEvent event) {
        // TODO cache this search
        Object value = event.getValue();
        if (value != null && event.getName().equals(value.getClass().getName())) {
            MutablePicoContainer container = (MutablePicoContainer) event.getSession().getAttribute(
                    PicoContainersProvider.CONTAINER_SESSION_KEY);
            if (container != null) {
                container.addComponent(value);
            } else {
                logger.debug("Ignoring attribute bounding because there is no container for attribute "
                        + event.getName());
            }
        }
    }

    public void attributeRemoved(HttpSessionBindingEvent event) {
        MutablePicoContainer container = (MutablePicoContainer) event.getSession().getAttribute(
                PicoContainersProvider.CONTAINER_SESSION_KEY);
        if (container != null) {
            container.removeComponent(event.getValue());
        } else {
            logger.debug("Ignoring attribute removal because there is no container for attribute " + event.getName());
        }
    }

    public void attributeReplaced(HttpSessionBindingEvent event) {
        MutablePicoContainer container = (MutablePicoContainer) event.getSession().getAttribute(
                PicoContainersProvider.CONTAINER_SESSION_KEY);
        if (container != null) {
            container.addComponent(event.getValue());
        } else {
            logger.debug("Ignoring attribute replacing because there is no container for attribute " + event.getName());
        }
    }

}
