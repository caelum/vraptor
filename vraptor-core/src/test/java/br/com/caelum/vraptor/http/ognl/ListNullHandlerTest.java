/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
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
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.http.ognl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import ognl.Evaluation;
import ognl.SimpleNode;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class ListNullHandlerTest {

	private VRaptorMockery mockery;
	private ListNullHandler handler;
	private Container container;
	private Client client;
	private Evaluation evaluation;
	private EmptyElementsRemoval removal;

	public static class Client {
		@SuppressWarnings( { "unchecked" })
		private List nonGeneric = new ArrayList();

		@SuppressWarnings("unchecked")
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
		this.handler = new ListNullHandler();
		this.container = mockery.mock(Container.class);
		this.client = new Client();
		this.evaluation = mockery.mock(Evaluation.class);
		this.removal = mockery.mock(EmptyElementsRemoval.class);
		mockery.checking(new Expectations() {
			{
				allowing(container).instanceFor(EmptyElementsRemoval.class); will(returnValue(removal));
			}
		});
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
		handler.instantiate(container, client.nonGeneric, "2", evaluation);
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
		handler.instantiate(container, client.names, 2, evaluation);
		assertThat(client.names.size(), is(equalTo(3)));
		assertThat(client.names.get(2), is(notNullValue()));
		mockery.assertIsSatisfied();
	}
}
