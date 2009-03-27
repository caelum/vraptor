package br.com.caelum.vraptor.core;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.ioc.Container;

public class DefaultResult implements Result {
    
    private final HttpServletRequest request;
    private final Container container;

    public DefaultResult(HttpServletRequest request, Container container) {
        this.request = request;
        // TODO we could use the componentfactory here
        // its only used so different views (i.e. freemarker engined, email sender) can receive their components
        this.container = container;
    }

    public <T extends View> T use(Class<T> view) {
        return container.instanceFor(view);
    }
    
    public void include(String key, Object value) {
        request.setAttribute(key, value);
    }

}
