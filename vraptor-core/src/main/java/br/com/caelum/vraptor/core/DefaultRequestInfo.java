package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * @author Fabio Kung
 */
public class DefaultRequestInfo implements RequestInfo {
    private ResourceMethod resourceMethod;

    public ResourceMethod getResourceMethod() {
        return resourceMethod;
    }

    public void setResourceMethod(ResourceMethod resourceMethod) {
        this.resourceMethod = resourceMethod;
    }
}
