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

package br.com.caelum.vraptor.interceptor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.view.RequestOutjectMap;

public class ParametersInstantiatorInterceptorTest {

    private VRaptorMockery mockery;
    private ParametersInstantiatorInterceptor instantiator;
    private MethodInfo params;
    private ParametersProvider parametersProvider;
	private Validator validator;
	private Localization localization;
	private HttpServletRequest request;
	private InterceptorStack stack;
	private ResourceBundle bundle;
	private List<Message> errors ;

	@Before
	@SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.mockery = new VRaptorMockery();
        this.params = mockery.mock(MethodInfo.class);
        this.request = mockery.mock(HttpServletRequest.class);
        this.parametersProvider = mockery.mock(ParametersProvider.class);
        this.validator = mockery.mock(Validator.class);
        this.localization = mockery.localization();
        this.instantiator = new ParametersInstantiatorInterceptor(parametersProvider, params, validator, localization, request);
        this.stack = mockery.mock(InterceptorStack.class);
        this.bundle = localization.getBundle();

        Field errorsField = ParametersInstantiatorInterceptor.class.getDeclaredField("errors");
        errorsField.setAccessible(true);
		this.errors = (List<Message>) errorsField.get(this.instantiator);
    }

    class Component {
        void method() {
        }
        void otherMethod(int oneParam){
        }

        void oneMoreMethod(String a) {

        }
    }

    @Test
    public void shouldUseTheProvidedParameters() throws InterceptionException, IOException, NoSuchMethodException {
        final ResourceMethod method = mockery.methodFor(Component.class, "method");

        mockery.checking(new Expectations() {{
        	Object[] values = new Object[] { new Object() };

        	one(parametersProvider).getParametersFor(method, errors, bundle);
            will(returnValue(values));

            one(stack).next(method, null);
            one(params).setParameters(values);
        }});

        instantiator.intercept(stack, method, null);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldValidateParameters() throws Exception {
        final ResourceMethod method = mockery.methodFor(Component.class, "otherMethod", int.class);

        mockery.checking(new Expectations() {{
        	Object[] values = new Object[]{0};

        	one(parametersProvider).getParametersFor(method, errors, bundle);
        	will(doAll(addErrorsToList("error1"),returnValue(values)));

        	one(validator).addAll(errors);
            one(stack).next(method, null);
            one(params).setParameters(values);

            Map<String, String> params = new HashMap<String, String>();
            params.put("param1.id", "value1");
            params.put("param2.id", "value2");
            one(request).getParameterMap();will(returnValue(params));

            allowing(request).getAttribute("param1");will(returnValue("originalValue1"));
            allowing(request).getAttribute("param2");will(returnValue(null));
            allowing(request).setAttribute(with(equal("param2")), with(any(RequestOutjectMap.class)));
        }});

        instantiator.intercept(stack, method, null);
        mockery.assertIsSatisfied();
    }

    @Test(expected=RuntimeException.class)
    public void shouldThrowException() throws Exception {
        final ResourceMethod method = mockery.methodFor(Component.class, "method");

        mockery.checking(new Expectations() {{
        	one(parametersProvider).getParametersFor(method, errors, bundle);
        	will(throwException(new RuntimeException()));
        }});

        instantiator.intercept(stack, method, null);
        mockery.assertIsSatisfied();
    }

    private Action addErrorsToList(final String... messages) {
    	return new Action() {
			public void describeTo(Description description) {
		        description.appendText("add something to errors");
		    }

			public Object invoke(Invocation invocation) throws Throwable {
				for (String message : messages) {
					errors.add(new ValidationMessage(message, "test"));
				}
				return null;
			}

		};
    }
}
