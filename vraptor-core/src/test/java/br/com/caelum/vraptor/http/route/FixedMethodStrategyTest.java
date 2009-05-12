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
package br.com.caelum.vraptor.http.route;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import br.com.caelum.vraptor.http.route.DefaultRouterTest.Dog;
import br.com.caelum.vraptor.http.route.DefaultRouterTest.MyControl;
import br.com.caelum.vraptor.interceptor.VRaptorMatchers;
import br.com.caelum.vraptor.resource.HttpMethod;

public class FixedMethodStrategyTest {

	class Client {
		private Long id;
		private Client child;

		public Client(Long id) {
			this.id = id;
		}

		public Client getChild() {
			return child;
		}

		public Long getId() {
			return id;
		}
	}

	class TypeCreated {
		private Client client;

		public TypeCreated(Client c) {
			this.client = c;
		}

		public Client getClient() {
			return client;
		}
	}

	@Test
	public void shouldTranslateAsteriskAsEmpty() {
		FixedMethodStrategy strategy = new FixedMethodStrategy("/clients/.*", null, null, null);
		assertThat(strategy.urlFor(client(3L)), is(equalTo("/clients/")));
	}

	@Test
	public void shouldTranslatePatternArgs() {
		FixedMethodStrategy strategy = new FixedMethodStrategy("/clients/{client.id}", null, null, null);
		assertThat(strategy.urlFor(client(3L)), is(equalTo("/clients/3")));
	}

	@Test
	public void shouldTranslatePatternArgNullAsEmpty() {
		FixedMethodStrategy strategy = new FixedMethodStrategy("/clients/{client.id}", null, null, null);
		assertThat(strategy.urlFor(client(null)), is(equalTo("/clients/")));
	}

	@Test
	public void shouldTranslatePatternArgInternalNullAsEmpty() {
		FixedMethodStrategy strategy = new FixedMethodStrategy("/clients/{client.child.id}", null, null, null);
		assertThat(strategy.urlFor(client(null)), is(equalTo("/clients/")));
	}

	private TypeCreated client(Long id) {
		return new TypeCreated(new Client(id));
	}

	@Test
	public void canTranslate() {

		new Rules(router) {
			public void routes() {
				routeFor("/clients/add").is(MyControl.class).add(null);
			}
		};
		assertThat(router.parse("/clients/add", HttpMethod.POST, request), is(VRaptorMatchers.resourceMethod(method(
				"add", Dog.class))));

	}

}
