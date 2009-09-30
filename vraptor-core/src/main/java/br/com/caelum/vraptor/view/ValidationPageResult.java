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

package br.com.caelum.vraptor.view;

import java.lang.reflect.Method;
import java.util.List;

import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationError;

/**
 * Default page result implementation.
 *
 * @author Guilherme Silveira
 */
public class ValidationPageResult implements PageResult {


    private final List<Message> errors;
	private final PageResult delegate;
	private final Proxifier proxifier;

	public ValidationPageResult(PageResult delegate, Proxifier proxifier, List<Message> errors) {
		this.proxifier = proxifier;
		this.delegate = delegate;
		this.errors = errors;
    }

    public void forward() {
    	delegate.forward();
    	throwException();
    }

	private void throwException() throws ValidationError {
		throw new ValidationError(errors);
	}

    public void include() {
    	delegate.include();
    	throwException();
    }

    public void redirect(String url) {
    	delegate.redirect(url);
    	throwException();
    }

	public void forward(String url) {
        delegate.forward(url);
        throwException();
	}

	public <T> T of(final Class<T> controllerType) {
		return proxifier.proxify(controllerType, new MethodInvocation<T>() {
            public T intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
            	try {
					method.invoke(delegate.of(controllerType), args);
				} catch (Exception e) {
					throw new ResultException(e);
				}
            	throw new ValidationError(errors);
            }
        });
	}

}
