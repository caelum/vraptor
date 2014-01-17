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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.DogController;

public class InstantiateInterceptorTest {

	private @Mock InterceptorStack stack;
	private @Mock ResourceMethod method;
	
	@Before
	public void setup() {
	MockitoAnnotations.initMocks(this);
	}

	@Test
	public void shouldAcceptAlways() {
		assertTrue(new InstantiateInterceptor(null).accepts(null));
	}
	
	@Test
	public void shouldUseContainerForNewComponent() throws InterceptionException, IOException {
	final DogController myDog = new DogController();
	InstanceContainer container = new InstanceContainer(myDog);
	InstantiateInterceptor interceptor = new InstantiateInterceptor(container);
	
	when(method.getResource()).thenReturn(new DefaultResourceClass(DogController.class));
	
	interceptor.intercept(stack, method, null);
	assertTrue(container.isEmpty());
	
	verify(stack).next(method, myDog);
	}

	@Test
	public void shouldNotInstantiateIfThereIsAlreadyAResource() throws InterceptionException, IOException {
	final DogController myDog = new DogController();
	InstantiateInterceptor interceptor = new InstantiateInterceptor(null);
	
	interceptor.intercept(stack, method, myDog);
	verify(stack).next(method, myDog);
	}
}
