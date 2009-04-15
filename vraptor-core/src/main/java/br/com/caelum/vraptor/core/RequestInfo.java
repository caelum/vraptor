package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * @author Fabio Kung
 */
public interface RequestInfo {
    ResourceMethod getResourceMethod();

    void setResourceMethod(ResourceMethod resourceMethod);
}
