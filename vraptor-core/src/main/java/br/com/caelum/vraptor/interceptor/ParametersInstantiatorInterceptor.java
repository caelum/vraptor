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
package br.com.caelum.vraptor.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationError;
import br.com.caelum.vraptor.validator.Validations;

/**
 * An interceptor which instantiates parameters and provide them to the stack.
 *
 * @author Guilherme Silveira
 */
public class ParametersInstantiatorInterceptor implements Interceptor {

    private final ParametersProvider provider;
    private final MethodInfo parameters;

    private static final Logger logger = LoggerFactory.getLogger(ParametersInstantiatorInterceptor.class);
    private final Validator validator;
    private final Localization localization;

    public ParametersInstantiatorInterceptor(ParametersProvider provider, MethodInfo parameters,
            Validator validator, Localization localization) {
        this.provider = provider;
        this.parameters = parameters;
        this.validator = validator;
        this.localization = localization;
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
            throws InterceptionException {
        final List<Message> errors = new ArrayList<Message>();
        Object[] values = provider.getParametersFor(method, errors, localization.getBundle());
        if (!errors.isEmpty()) {
            try {
                validator.checking(new Validations() {
                    @Override
                    public List<Message> getErrors() {
                        return errors;
                    }
                });
            } catch (ValidationError e) {
                // just fine... i was expecting that
                return;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Parameter values for " + method + " are " + Arrays.asList(values));
        }
        parameters.setParameters(values);
        stack.next(method, resourceInstance);
    }

}
