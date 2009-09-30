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
package br.com.caelum.vraptor.vraptor2;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.validator.NotNull;
import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.vraptor.plugin.hibernate.Validate;
import org.vraptor.validator.ValidationErrors;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class HibernateValidatorPluginInterceptorTest {

    private VRaptorMockery mockery;
    private MethodInfo parameters;
    private HttpServletRequest request;
    private ParameterNameProvider provider;
    private ValidationErrors errors;
    private HibernateValidatorPluginInterceptor interceptor;
    private InterceptorStack stack;
	private Localization localization;

    @Before
    public void setup() {
        this.mockery = new VRaptorMockery();
        this.localization = mockery.localization();
        this.interceptor = new HibernateValidatorPluginInterceptor(errors, provider, request, parameters, localization);
        this.stack = mockery.mock(InterceptorStack.class);
    }

    class Door {
        @NotNull
        String color;
    }
    class Window {
    }
    class Car {
        void paintWithoutValidate(Door door) {
        }
        @Validate
        void paint(Window w) {
        }
    }

    @Test
    public void shouldDoNothingIfTheMethodShouldNotBeValidatedButTheParamIsUnvalid() throws NoSuchMethodException,
            InterceptionException, IOException {
        final ResourceMethod method = mockery.methodFor(Car.class, "paintWithoutValidate", Door.class);
        mockery.checking(new Expectations() {
            {
                one(stack).next(method, null);
            }
        });
        interceptor.intercept(stack, method, null);
        mockery.assertIsSatisfied();

    }

    @Test
    public void shouldDoNothingIfTheMethodAsksForValidationOfNoParams() throws NoSuchMethodException,
            InterceptionException, IOException {
        final ResourceMethod method = mockery.methodFor(Car.class, "paintWithoutValidate", Door.class);
        mockery.checking(new Expectations() {
            {
                one(stack).next(method, null);
            }
        });
        interceptor.intercept(stack, method, null);
        mockery.assertIsSatisfied();

    }

}
