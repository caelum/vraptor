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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ognl.Evaluation;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.SimpleNode;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.proxy.CglibProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.ReflectionInstanceCreator;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class ListNullHandlerTest {

	private VRaptorMockery mockery;
	private ListNullHandler handler;
	private Container container;
	private Client client;
	private Evaluation evaluation;
	private EmptyElementsRemoval removal;
	private OgnlContext context;

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
		this.mockery = new VRaptorMockery(true);
		this.client = new Client();
		this.evaluation = mockery.mock(Evaluation.class);
		this.removal = mockery.mock(EmptyElementsRemoval.class);
		this.handler = new ListNullHandler(removal);
		
        context = (OgnlContext) Ognl.createDefaultContext(null);
        context.put("proxifier", new CglibProxifier(new ReflectionInstanceCreator()));
	}

	@Test(expected = VRaptorException.class)
	public void shouldNotSupportNonGenericLists() throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		final SimpleNode node = mockery.ognlNode("nonGeneric");
		mockery.checking(new Expectations() {
			{
				one(evaluation).getNode();
				will(returnValue(node));
				one(evaluation).getSource();
				will(returnValue(client));
			}
		});
		handler.instantiate(client.nonGeneric, "2", handler.getListType(client.nonGeneric, evaluation, context));
		mockery.assertIsSatisfied();
	}

	@Test
	public void shouldInsertItemsUntilTheRequiredPosition() throws InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		final SimpleNode node = mockery.ognlNode("names");
		mockery.checking(new Expectations() {
			{
				one(evaluation).getNode();
				will(returnValue(node));
				one(evaluation).getSource();
				will(returnValue(client));
				one(removal).add(client.names);
			}
		});
		handler.instantiate(client.names, 2, handler.getListType(client.names, evaluation, context));
		assertThat(client.names.size(), is(equalTo(3)));
		assertThat(client.names.get(2), is(notNullValue()));
		mockery.assertIsSatisfied();
	}
}
