
package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Represents all method parameters, and the result returned by the invoked method.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
public interface MethodInfo {
    ResourceMethod getResourceMethod();
    void setResourceMethod(ResourceMethod resourceMethod);
    void setParameters(Object[] parameters);
    Object[] getParameters();
    Object getResult();
    void setResult(Object result);
}
