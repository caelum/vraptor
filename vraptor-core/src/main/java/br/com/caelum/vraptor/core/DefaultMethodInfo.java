/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * Holder for method being invoked and parameters being passed.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
@RequestScoped
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

	public boolean parametersWereSet() {
		return parameters != null;
	}
}
