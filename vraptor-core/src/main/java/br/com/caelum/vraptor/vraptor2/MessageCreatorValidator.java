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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.vraptor.i18n.FixedMessage;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationError;
import br.com.caelum.vraptor.validator.Validations;
import br.com.caelum.vraptor.view.Results;
import br.com.caelum.vraptor.view.ValidationViewsFactory;

/**
 * The vraptor2 compatible messages creator.
 *
 * @author Guilherme Silveira
 */
@RequestScoped
public class MessageCreatorValidator implements Validator {

    private final Result result;
    private final ValidationErrors errors;

	private final ResourceMethod resource;
	private final MethodInfo info;
	private boolean containsErrors;
	private final ValidationViewsFactory viewsFactory;

    public MessageCreatorValidator(Result result, ValidationErrors errors, MethodInfo info, ValidationViewsFactory viewsFactory) {
        this.result = result;
        this.errors = errors;
		this.viewsFactory = viewsFactory;
		this.resource = info.getResourceMethod();
		this.info = info;
    }

    public void checking(Validations validations) {
        List<Message> messages = validations.getErrors();
        for (Message s : messages) {
        	add(s);
        }
    }

    public <T extends View> T onErrorUse(Class<T> view) {
    	if (!hasErrors()) {
    		return new MockResult().use(view); //ignore anything, no errors occurred
    	}
    	result.include("errors", errors);
    	if(Info.isOldComponent(resource.getResource())) {
    		info.setResult("invalid");
    		result.use(Results.page()).forward();
    		throw new ValidationError(new ArrayList<Message>());
    	} else {
    		return viewsFactory.instanceFor(view, new ArrayList<Message>());
    	}
    }

    private void add(Message message) {
		containsErrors = true;
        this.errors.add(new FixedMessage(message.getCategory(), message.getMessage(), message.getCategory()));
	}

	public void add(Collection<? extends Message> messages) {
		for (Message message : messages) {
			this.add(message);
		}
	}

	public boolean hasErrors() {
		return containsErrors;
	}
}
