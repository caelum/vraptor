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
package br.com.caelum.vraptor.ioc.pico;

import static java.util.Arrays.asList;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.interceptor.InterceptorSequence;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * @author Fabio Kung
 */
public class InterceptorRegistrarTest {

    private Mockery mockery;
    private InterceptorRegistrar registrar;
    private InterceptorRegistry registry;
    private Scanner scanner;

    @Before
    public void setup() {
        mockery = new Mockery();
        registry = mockery.mock(InterceptorRegistry.class);
        scanner = mockery.mock(Scanner.class);
        registrar = new InterceptorRegistrar(registry);
    }

    @Test(expected = VRaptorException.class)
    public void shouldFailIfClassAnnotatedWithInterceptsDoesntImplementInterceptorOrInterceptorSequence() {
        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithAnnotation(Intercepts.class);
                will(returnValue(asList(WrongInterceptor.class)));
            }
        });
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldAddAllInterceptorsFromASequenceInItsOrder() {

        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithAnnotation(Intercepts.class);
                will(returnValue(asList(MySequence.class)));
                one(registry).register(AnnotatedInterceptor.class, NotAnnotatedInterceptor.class);
            }
        });
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Test
    public void shouldAcceptInterceptorsAnnotatedWithInterceptorAnnotation() {
        mockery.checking(new Expectations() {
            {
                one(scanner).getTypesWithAnnotation(Intercepts.class);
                will(returnValue(asList(AnnotatedInterceptor.class)));
                one(registry).register(AnnotatedInterceptor.class);
            }
        });
        registrar.registerFrom(scanner);
        mockery.assertIsSatisfied();
    }

    @Intercepts
    public static class WrongInterceptor {
    }

    @Intercepts
    public static class MySequence implements InterceptorSequence {

        @SuppressWarnings("unchecked")
        public Class<? extends Interceptor>[] getSequence() {
            return new Class[]{AnnotatedInterceptor.class, NotAnnotatedInterceptor.class};
        }
    }

    @Intercepts
    public static class AnnotatedInterceptor implements Interceptor {
        public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
                throws InterceptionException {
        }

        public boolean accepts(ResourceMethod method) {
            return true;
        }
    }

    public static class NotAnnotatedInterceptor implements Interceptor {
        public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance)
                throws InterceptionException {
        }

        public boolean accepts(ResourceMethod method) {
            return true;
        }
    }
}
