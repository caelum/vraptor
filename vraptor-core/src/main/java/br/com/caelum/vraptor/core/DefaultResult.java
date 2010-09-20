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


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.proxy.Proxifier;

@RequestScoped
public class DefaultResult extends AbstractResult {

    private final HttpServletRequest request;
    private final Container container;
    private final Map<String, Object> includedAttributes;
    private boolean responseCommitted = false;
    private final Proxifier proxifier;
    private final ExceptionMapper exceptions;

    public DefaultResult(HttpServletRequest request, Container container, Proxifier proxifier, ExceptionMapper exceptions) {
        this.request = request;
        this.container = container;
        this.includedAttributes = new HashMap<String, Object>();
        this.proxifier = proxifier;
        this.exceptions = exceptions;
    }

    public <T extends View> T use(Class<T> view) {
        this.responseCommitted = true;
        return container.instanceFor(view);
    }
    
    public Result on(Class<? extends Exception> exception) {
        if (exception == null) {
            throw new NullPointerException("Exception cannot be null.");
        }

        ExceptionRecorder<Result> instance = new ExceptionRecorder<Result>(proxifier);
        exceptions.add(exception, instance);

        return proxifier.proxify(Result.class, instance);
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
}
