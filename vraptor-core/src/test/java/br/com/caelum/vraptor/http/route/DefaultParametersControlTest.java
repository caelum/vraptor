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

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class DefaultParametersControlTest {

	private VRaptorMockery mockery;
	private MutableRequest request;

	@Before
	public void setup() {
		this.mockery = new VRaptorMockery();
		this.request = mockery.mock(MutableRequest.class);
	}

	@Test
	public void registerExtraParametersFromAcessedUrl() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{dog.id}");
		mockery.checking(new Expectations() {
			{
				one(request).setParameter("dog.id", new String[] {"45"});
			}
		});
		control.fillIntoRequest("/clients/45", request);
		mockery.assertIsSatisfied();
	}

	@Test
	public void worksAsRegexWhenUsingParameters() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{dog.id}");
		assertThat(control.matches("/clients/15"), is(equalTo(true)));
		mockery.assertIsSatisfied();
	}

	@Test
	public void worksWithBasicRegexEvaluation() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients.*");
		assertThat(control.matches("/clientsWhatever"), is(equalTo(true)));
		mockery.assertIsSatisfied();
	}


	class Client {
		private final Long id;
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
		private final Client client;

		public TypeCreated(Client c) {
			this.client = c;
		}

		public Client getClient() {
			return client;
		}
	}

	@Test
	public void shouldTranslateAsteriskAsEmpty() {
		String uri = new DefaultParametersControl("/clients/.*").fillUri(client(3L));
		assertThat(uri, is(equalTo("/clients/")));
	}

	@Test
	public void shouldTranslatePatternArgs() {
		String uri = new DefaultParametersControl("/clients/{client.id}").fillUri(client(3L));
		assertThat(uri, is(equalTo("/clients/3")));
	}

	@Test
	public void shouldTranslatePatternArgNullAsEmpty() {
		String uri = new DefaultParametersControl("/clients/{client.id}").fillUri(client(null));
		assertThat(uri, is(equalTo("/clients/")));
	}

	@Test
	public void shouldTranslatePatternArgInternalNullAsEmpty() {
		String uri = new DefaultParametersControl("/clients/{client.child.id}") .fillUri(client(null));
		assertThat(uri, is(equalTo("/clients/")));
	}

	@Test
	public void shouldMatchPatternLazily() throws Exception {
		DefaultParametersControl wrong = new DefaultParametersControl("/clients/{client.id}/");
		DefaultParametersControl right = new DefaultParametersControl("/clients/{client.id}/subtask/");
		String uri = "/clients/3/subtask/";

		assertThat(wrong.matches(uri), is(false));
		assertThat(right.matches(uri), is(true));

	}

	@Test
	public void shouldMatchMoreThanOneVariable() throws Exception {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{client.id}/subtask/{task.id}/");

		assertThat(control.matches("/clients/3/subtask/5/"), is(true));
	}
	private TypeCreated client(Long id) {
		return new TypeCreated(new Client(id));
	}

	@Test
	public void shouldBeGreedyWhenIPutAnAsteriskOnExpression() throws Exception {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{pathToFile*}");

		assertThat(control.matches("/clients/my/path/to/file/"), is(true));
	}
	@Test
	public void shouldNotBeGreedyAtPatternCompiling() throws Exception {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{client.id}/view/{filename*}");

		assertThat(control.matches("/clients/1/view/my/path/to/file/"), is(true));
		mockery.checking(new Expectations() {
			{
				one(request).setParameter("client.id", "1");
				one(request).setParameter("filename", new String[] {"my/path/to/file/"});
			}
		});
		control.fillIntoRequest("/clients/1/view/my/path/to/file/", request);
		mockery.assertIsSatisfied();
	}

	@Test
	public void registerExtraParametersFromAcessedUrlWithGreedyParameters() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{pathToFile*}");
		mockery.checking(new Expectations() {
			{
				one(request).setParameter("pathToFile", new String[] {"my/path/to/file"});
			}
		});
		control.fillIntoRequest("/clients/my/path/to/file", request);
		mockery.assertIsSatisfied();
	}
}
