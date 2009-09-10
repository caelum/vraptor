/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.validator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.view.ResultException;
import br.com.caelum.vraptor.view.Results;

/**
 * The default validator implementation.
 *
 * @author Guilherme Silveira
 */
@RequestScoped
public class DefaultValidator implements Validator {

    private final Proxifier proxifier;
    private final Result result;

    private Object[] argsToUse;
    private Method method;
    private Class<?> typeToUse;

	private final List<Message> errors = new ArrayList<Message>();

    public DefaultValidator(Proxifier proxifier, Result result) {
        this.proxifier = proxifier;
        this.result = result;
    }

    // TODO: do not use String consequences anymore
    // TODO: on error action should be defined by the onError method
    public void checking(Validations validations) {
        add(validations.getErrors());
    }


    public <T extends View> T onErrorUse(Class<T> view) {
    	if (!hasErrors()) {
    		return new MockResult().use(view); //ignore anything
    	}
    	result.include("errors", errors);
    	return result.use(view);
    }

    public void add(Collection<? extends Message> message) {
		this.errors.addAll(message);
	}

	// runs the validation
	private void validate() {
        if (hasErrors()) {
            if (method != null) {
                Object instance = result.use(Results.logic()).forwardTo(typeToUse);
                try {
                    method.invoke(instance, argsToUse);
                } catch (Exception e) {
                    throw new ResultException(e);
                }
            } else {
            	throw new ResultException("You must call goTo method in order to specify where to go");
//            	result.use(Results.page()).forward(uriWithoutContextPath());
            }
            // finished just fine
            throw new ValidationError(errors);
        }
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

//	private String uriWithoutContextPath() {
//		return request.getRequestURI().replaceFirst(request.getContextPath(), "");
//	}

}
