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
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * A basic implementation of a Result
 * @author guilherme silveira
 */
@RequestScoped
public class DefaultResult extends AbstractResult {

    private final HttpServletRequest request;
    private final Container container;
    private final Map<String, Object> includedAttributes;
    private boolean responseCommitted = false;
    private final ExceptionMapper exceptions;
	private final TypeNameExtractor extractor;

    public DefaultResult(HttpServletRequest request, Container container, ExceptionMapper exceptions, TypeNameExtractor extractor) {
        this.request = request;
        this.container = container;
		this.extractor = extractor;
        this.includedAttributes = new HashMap<String, Object>();
        this.exceptions = exceptions;
    }

    public <T extends View> T use(Class<T> view) {
        this.responseCommitted = true;
        return container.instanceFor(view);
    }
    
    public Result on(Class<? extends Exception> exception) {
        return exceptions.record(exception);
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

	public Result include(Object value) {
		if(value == null) {
			return this;
		}
		
		String key = this.extractor.nameFor(value.getClass());
		return include(key, value);
	}
}
