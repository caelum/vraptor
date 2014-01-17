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

package br.com.caelum.vraptor.http.ognl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import ognl.Evaluation;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.SimpleNode;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.ReflectionInstanceCreator;

public class ListNullHandlerTest {

	private ListNullHandler handler;
	private Client client;
	private OgnlContext context;
	private @Mock Evaluation evaluation;
	private @Mock EmptyElementsRemoval removal;

	public static class Client {
		private List nonGeneric = new ArrayList();

		public void setNonGeneric(List nonGeneric) {
			this.nonGeneric = nonGeneric;
		}

		public void setNames(List<Client> names) {
			this.names = names;
		}

		public List<Client> getNames() {
			return names;
		}

		private List<Client> names = new ArrayList<Client>();
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		client = new Client();
		handler = new ListNullHandler(removal);
		
	context = (OgnlContext) Ognl.createDefaultContext(null);
	context.put("proxifier", new JavassistProxifier(new ReflectionInstanceCreator()));
	}

	@Test(expected = VRaptorException.class)
	public void shouldNotSupportNonGenericLists() throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		SimpleNode node = mock(SimpleNode.class);
		when(node.toString()).thenReturn("nonGeneric");
		
		when(evaluation.getNode()).thenReturn(node);
		when(evaluation.getSource()).thenReturn(client);

		handler.instantiate(client.nonGeneric, "2", handler.getListType(client.nonGeneric, evaluation, context));
	}

	@Test
	public void shouldInsertItemsUntilTheRequiredPosition() throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		SimpleNode node = mock(SimpleNode.class);
		when(node.toString()).thenReturn("names");
		
		when(evaluation.getNode()).thenReturn(node);
		when(evaluation.getSource()).thenReturn(client);
				
		handler.instantiate(client.names, 2, handler.getListType(client.names, evaluation, context));
		assertThat(client.names.size(), is(equalTo(3)));
		assertThat(client.names.get(2), is(notNullValue()));

		verify(removal).add(client.names);
	}
}
