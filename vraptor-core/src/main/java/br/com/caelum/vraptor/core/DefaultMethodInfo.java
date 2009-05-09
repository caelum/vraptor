package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Holder for method being invoked and parameters being passed.
 * 
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
public class DefaultMethodInfo implements MethodInfo {
	private ResourceMethod resourceMethod;
	private Object[] parameters;
	private Object result;

	public ResourceMethod getResourceMethod() {
		return resourceMethod;
	}

	public void setResourceMethod(ResourceMethod resourceMethod) {
		this.resourceMethod = resourceMethod;
	}

	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}

	public Object[] getParameters() {
		return parameters;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
