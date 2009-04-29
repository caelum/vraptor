package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Represents all method parameters
 * 
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
public interface MethodInfo {
    ResourceMethod getResourceMethod();

    void setResourceMethod(ResourceMethod resourceMethod);
    void setParameters(Object[] parameters, String[] names);

    Object[] getParameters();
}
