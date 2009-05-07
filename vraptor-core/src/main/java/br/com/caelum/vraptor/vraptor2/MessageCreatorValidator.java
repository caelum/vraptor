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
package br.com.caelum.vraptor.vraptor2;

import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.vraptor.i18n.FixedMessage;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationError;
import br.com.caelum.vraptor.validator.Validations;
import br.com.caelum.vraptor.view.LogicResult;
import br.com.caelum.vraptor.view.PageResult;
import br.com.caelum.vraptor.view.ResultException;

/**
 * The vraptor2 compatible messages creator.
 * 
 * @author Guilherme Silveira
 */
@RequestScoped
public class MessageCreatorValidator implements Validator {

	private final PageResult result;
	private final ValidationErrors errors;

	private Object[] argsToUse;
	private Method method;

	private final LogicResult logic;

	private Class<?> typeToUse;

	public MessageCreatorValidator(PageResult result, ValidationErrors errors, LogicResult logic) {
		this.result = result;
		this.errors = errors;
		this.logic = logic;
	}

	public void checking(Validations validations) {
		List<Message> messages = validations.getErrors();
		if (!messages.isEmpty()) {
			if (method != null) {
				Object instance = logic.redirectServerTo(typeToUse);
				try {
					method.invoke(instance, argsToUse);
				} catch (Exception e) {
					throw new ResultException(e);
				}
			} else {
				for (Message s : messages) {
					this.errors.add(new FixedMessage(s.getCategory(), s.getMessage(), s.getCategory()));
				}
				result.include("errors", this.errors);
				result.forward("invalid");
				// finished just fine
			}
			throw new ValidationError(messages);
		}
	}

	public Validator onError() {
		return this;
	}

	public <T> T goTo(Class<T> type) {
		this.typeToUse = type;
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(type);
		enhancer.setCallback(new MethodInterceptor() {
			public Object intercept(Object instance, Method method, Object[] args, MethodProxy proxy) throws Throwable {
				MessageCreatorValidator.this.argsToUse = args;
				MessageCreatorValidator.this.method = method;
				return null;
			}
		});
		return (T) enhancer.create();
	}

}
