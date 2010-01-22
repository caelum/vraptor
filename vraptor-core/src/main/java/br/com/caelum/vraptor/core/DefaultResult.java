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

import static br.com.caelum.vraptor.view.Results.logic;
import static br.com.caelum.vraptor.view.Results.page;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.RequestScoped;

@RequestScoped
public class DefaultResult implements Result {

    private final HttpServletRequest request;
    private final Container container;
    private final Map<String, Object> includedAttributes;
    private boolean responseCommitted = false;

    public DefaultResult(HttpServletRequest request, Container container) {
        this.request = request;
        this.container = container;
        this.includedAttributes = new HashMap<String, Object>();
    }

    public <T extends View> T use(Class<T> view) {
        this.responseCommitted = true;
        return container.instanceFor(view);
    }

    public Result include(String key, Object value) {
    	includedAttributes.put(key, value);
        request.setAttribute(key, value);
        return this;
    }

    public boolean used() {
        return responseCommitted;
    }

	public Map<String, Object> included() {
		return Collections.unmodifiableMap(includedAttributes);
	}

	public void forwardTo(String uri) {
		use(page()).forward(uri);
	}

	public <T> T forwardTo(Class<T> controller) {
		return use(logic()).forwardTo(controller);
	}

	public <T> T redirectTo(Class<T> controller) {
		return use(logic()).redirectTo(controller);
	}

	public <T> T of(Class<T> controller) {
		return use(page()).of(controller);
	}

	@SuppressWarnings("unchecked")
	public <T> T redirectTo(T controller) {
		return (T) redirectTo(controller.getClass());
	}

	@SuppressWarnings("unchecked")
	public <T> T forwardTo(T controller) {
		return (T) forwardTo(controller.getClass());
	}

	@SuppressWarnings("unchecked")
	public <T> T of(T controller) {
		return (T) of(controller.getClass());
	}
}
