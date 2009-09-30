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

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.DogController;

public class InstantiateInterceptorTest {

    private Mockery mockery;
    private Container container;

    @Before
    public void setup() {
        this.mockery = new Mockery();
        this.container = mockery.mock(Container.class);
    }

    @Test
    public void shouldUseContainerForNewComponent() throws InterceptionException, IOException {
        InstantiateInterceptor interceptor = new InstantiateInterceptor(container);
        final InterceptorStack stack = mockery.mock(InterceptorStack.class);
        final DogController myDog = new DogController();
        final ResourceMethod method = mockery.mock(ResourceMethod.class);
        mockery.checking(new Expectations() {
            {
                one(container).instanceFor(DogController.class);
                will(returnValue(myDog));
                one(stack).next(method, myDog);
                one(method).getResource(); will(returnValue(new DefaultResourceClass(DogController.class)));
            }
        });
        interceptor.intercept(stack, method, null);
        mockery.assertIsSatisfied();
    }

}
