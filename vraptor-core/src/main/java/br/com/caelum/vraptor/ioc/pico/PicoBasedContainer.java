package br.com.caelum.vraptor.ioc.pico;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.DefaultRequestExecution;
import br.com.caelum.vraptor.core.DefaultResult;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.http.OgnlParametersProvider;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceRegistry;
import br.com.caelum.vraptor.view.jsp.JspView;

public class PicoBasedContainer implements Container {

    private final MutablePicoContainer container;

    public PicoBasedContainer(MutablePicoContainer parent, VRaptorRequest request, ResourceRegistry resources) {
        this.container = new PicoBuilder(parent).withCaching().build();
        // TODO try to remove addComponent(this) - InstantiateInterceptor and InterceptorStack
        // needs to instantiate objects with dependency injection
        this.container.addComponent(this);
        this.container.addComponent(request).addComponent(request.getRequest()).addComponent(request.getResponse());
        this.container.addComponent(DefaultInterceptorStack.class).addComponent(DefaultRequestExecution.class);
        this.container.addComponent(ResourceLookupInterceptor.class).addComponent(InstantiateInterceptor.class);
        this.container.addComponent(DefaultResult.class).addComponent(ExecuteMethodInterceptor.class);
        this.container.addComponent(JspView.class);
        this.container.addComponent(OgnlParametersProvider.class);
        for (Resource resource : resources.all()) {
            this.container.addComponent(resource.getType());
        }
    }

    public <T> T instanceFor(Class<T> type) {
        return container.getComponent(type);
    }

    public void register(Object instance) {
        this.container.addComponent(instance);
    }

}
