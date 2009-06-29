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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
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
	private final HttpServletRequest request;

	private final List<Message> errors = new ArrayList<Message>();

    public DefaultValidator(Proxifier proxifier, Result result, HttpServletRequest request) {
        this.proxifier = proxifier;
        this.result = result;
		this.request = request;
    }

    // TODO: do not use String consequences anymore
    // TODO: on error action should be defined by the onError method
    public void checking(Validations validations) {
        this.errors.addAll(validations.getErrors());
        validate();
    }

    public Validator onError() {
        return this;
    }

    public <T> T goTo(Class<T> type) {
        this.typeToUse = type;
        return proxifier.proxify(type, new MethodInvocation<T>() {

			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
                if (DefaultValidator.this.method == null) {
                    DefaultValidator.this.argsToUse = args;
                    DefaultValidator.this.method = method;
                }
                return null;
            }
        });
    }

	public void add(Message message) {
		this.errors.add(message);
	}

	public void validate() {
        if (!errors.isEmpty()) {
            result.include("errors", errors);
            if (method != null) {
                Object instance = result.use(Results.logic()).forwardTo(typeToUse);
                try {
                    method.invoke(instance, argsToUse);
                } catch (Exception e) {
                    throw new ResultException(e);
                }
            } else {
            	result.use(Results.page()).forward(uriWithoutContextPath());
            }
            // finished just fine
            throw new ValidationError(errors);
        }
	}

	private String uriWithoutContextPath() {
		return request.getRequestURI().replaceFirst(request.getContextPath(), "");
	}

}
