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

package br.com.caelum.vraptor.http.route;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.http.asm.AsmBasedTypeCreator;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

import com.google.common.collect.ImmutableMap;

public class DefaultParametersControlTest {

	private @Mock MutableRequest request;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void registerExtraParametersFromAcessedUrl() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{dog.id}");
		control.fillIntoRequest("/clients/45", request);
		verify(request).setParameter("dog.id", new String[] {"45"});
	}

	@Test
	public void registerParametersWithAsterisks() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{my.path*}");
		control.fillIntoRequest("/clients/one/path", request);
		verify(request).setParameter("my.path", new String[] {"one/path"});
	}

	@Test
	public void registerParametersWithRegexes() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{hexa:[0-9A-Z]+}");

		control.fillIntoRequest("/clients/FAF323", request);

		verify(request).setParameter("hexa", new String[] {"FAF323"});
	}

	@Test
	public void worksAsRegexWhenUsingParameters() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{dog.id}");
		assertThat(control.matches("/clients/15"), is(equalTo(true)));
	}

	@Test
	public void worksWithBasicRegexEvaluation() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients.*");
		assertThat(control.matches("/clientsWhatever"), is(equalTo(true)));
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
		DefaultParametersControl control = new DefaultParametersControl("/project/{project.name}/build/{buildId}/view/{filename*}");

		String uri = "/project/Vraptor3/build/12345/view/artifacts/vraptor.jar";
		assertThat(control.matches(uri), is(true));

		control.fillIntoRequest(uri, request);

		verify(request).setParameter("project.name", "Vraptor3");
		verify(request).setParameter("filename", new String[] {"artifacts/vraptor.jar"});
		verify(request).setParameter("buildId", new String[] {"12345"});
		assertThat(control.apply(new String[] {"Vraptor3", "12345", "artifacts/vraptor.jar"}),
				is(uri));
	}

	@Test
	public void registerExtraParametersFromAcessedUrlWithGreedyParameters() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{pathToFile*}");

		control.fillIntoRequest("/clients/my/path/to/file", request);

		verify(request).setParameter("pathToFile", new String[] {"my/path/to/file"});
	}

	@Test
	public void registerExtraParametersFromAcessedUrlWithGreedyAndDottedParameters() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{path.to.file*}");

		control.fillIntoRequest("/clients/my/path/to/file", request);

		verify(request).setParameter("path.to.file", new String[] {"my/path/to/file"});
	}

	static class PathToFile {

		public void withPath(String pathToFile) {

		}
	}
	@Test
	public void fillURLWithGreedyParameters() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{pathToFile*}");
		ResourceMethod method = DefaultResourceMethod.instanceFor(PathToFile.class, PathToFile.class.getDeclaredMethods()[0]);
		Object object = new AsmBasedTypeCreator(new ParanamerNameProvider()).instanceWithParameters(method, "my/path/to/file");
		String filled = control.fillUri(object);
		assertThat(filled, is("/clients/my/path/to/file"));
	}
	@Test
	public void fillURLWithoutGreedyParameters() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{pathToFile}");
		ResourceMethod method = DefaultResourceMethod.instanceFor(PathToFile.class, PathToFile.class.getDeclaredMethods()[0]);
		Object object = new AsmBasedTypeCreator(new ParanamerNameProvider()).instanceWithParameters(method, "my/path/to/file");
		String filled = control.fillUri(object);
		assertThat(filled, is("/clients/my/path/to/file"));
	}

	@Test
	public void whenNoParameterPatternsAreGivenShouldMatchAnything() throws Exception {
		ParametersControl control = new DefaultParametersControl("/any/{aParameter}/what", Collections.<String,String>emptyMap());
		assertTrue(control.matches("/any/ICanPutAnythingInHere/what"));
	}
	@Test
	public void whenParameterPatternsAreGivenShouldMatchAccordingToGivenPatterns() throws Exception {
		ParametersControl control = new DefaultParametersControl("/any/{aParameter}/what",
				new ImmutableMap.Builder<String, String>().put("aParameter", "aaa\\d{3}bbb").build());
		assertFalse(control.matches("/any/ICantPutAnythingInHere/what"));
		assertFalse(control.matches("/any/aaa12bbb/what"));
		assertTrue(control.matches("/any/aaa123bbb/what"));
	}

	@Test
	public void shouldFillRequestWhenAPatternIsSpecified() throws Exception {
		DefaultParametersControl control = new DefaultParametersControl("/project/{project.id}/build/",
				new ImmutableMap.Builder<String, String>().put("project.id", "\\d+").build());

		String uri = "/project/15/build/";
		assertThat(control.matches(uri), is(true));

		control.fillIntoRequest(uri, request);

		verify(request).setParameter("project.id", "15");

		assertThat(control.apply(new String[] {"15"}),is(uri));
	}
}
