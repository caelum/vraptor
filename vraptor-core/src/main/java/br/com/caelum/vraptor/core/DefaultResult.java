
package br.com.caelum.vraptor.core;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.RequestScoped;

@RequestScoped
public class DefaultResult implements Result {

    private final HttpServletRequest request;
    private final Container container;
    private boolean went = false;

    public DefaultResult(HttpServletRequest request, Container container) {
        this.request = request;
        this.container = container;
    }

    public <T extends View> T use(Class<T> view) {
        this.went = true;
        return container.instanceFor(view);
    }

    public void include(String key, Object value) {
        request.setAttribute(key, value);
    }

    public boolean used() {
        return went;
    }

}
