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
package br.com.caelum.vraptor.ioc;

import static org.mockito.Mockito.verify;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.ComponentRegistry;


/**
 *
 * @author Lucas Cavalcanti
 *
 */
public class AbstractComponentRegistryTest {

	private @Mock ComponentRegistry registry;
	private AbstractComponentRegistry abstractRegistry;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		abstractRegistry = new AbstractComponentRegistry() {
			public void register(Class<?> requiredType, Class<?> componentType) {
				registry.register(requiredType, componentType);
			}
		};
	}
	@Test
	public void shouldRegisterComponentAndImplementedInterfaces() {
	abstractRegistry.deepRegister(RunnableComponent.class);
	
	verify(registry).register(Runnable.class, RunnableComponent.class);
	verify(registry).register(RunnableComponent.class, RunnableComponent.class);
	}

	@Test
	public void shouldRegisterComponentUsingAllPossibleSupertypes() {
	abstractRegistry.deepRegister(ArrayListSubclass.class);
	
	verify(registry).register(ArrayListSubclass.class, ArrayListSubclass.class);
	verify(registry).register(ArrayList.class, ArrayListSubclass.class);
	verify(registry).register(List.class, ArrayListSubclass.class);
	verify(registry).register(Collection.class, ArrayListSubclass.class);
	verify(registry).register(Iterable.class, ArrayListSubclass.class);
	verify(registry).register(Cloneable.class, ArrayListSubclass.class);
	verify(registry).register(Serializable.class, ArrayListSubclass.class);
	verify(registry).register(RandomAccess.class, ArrayListSubclass.class);
	verify(registry).register(AbstractList.class, ArrayListSubclass.class);
	verify(registry).register(AbstractCollection.class, ArrayListSubclass.class);
	}


	@Component
	static class RunnableComponent implements Runnable {
	public void run() {
	}
	}

	@SuppressWarnings("serial")
	@Component
	public static class ArrayListSubclass extends ArrayList<Object> {
	}
}
