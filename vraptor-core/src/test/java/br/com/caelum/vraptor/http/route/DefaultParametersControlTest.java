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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.TwoWayConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.MutableRequest;

import com.google.common.collect.ImmutableMap;

public class DefaultParametersControlTest {

	private @Mock MutableRequest request;
	private @Mock Converters converters;
	private @Mock TwoWayConverter converter;
    private Evaluator evaluator = new JavaEvaluator();

    @Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void registerExtraParametersFromAcessedUrl() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{dog.id}", converters, evaluator);
		control.fillIntoRequest("/clients/45", request);
		verify(request).setParameter("dog.id", new String[] {"45"});
	}

	@Test
	public void registerParametersWithAsterisks() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{my.path*}", converters, evaluator);
		control.fillIntoRequest("/clients/one/path", request);
		verify(request).setParameter("my.path", new String[] {"one/path"});
	}

	@Test
	public void registerParametersWithRegexes() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{hexa:[0-9A-Z]+}", converters, evaluator);

		control.fillIntoRequest("/clients/FAF323", request);

		verify(request).setParameter("hexa", new String[] {"FAF323"});
	}

	@Test
	public void registerParametersWithMultipleRegexes() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/test/{hash1:[a-z0-9]{16}}{id}{hash2:[a-z0-9]{16}}/", Collections.singletonMap("id", "\\d+"), converters, evaluator);

		control.fillIntoRequest("/test/0123456789abcdef1234fedcba9876543210/", request);

		verify(request).setParameter("hash1", new String[] {"0123456789abcdef"});
		verify(request).setParameter("id", new String[] {"1234"});
		verify(request).setParameter("hash2", new String[] {"fedcba9876543210"});
	}

	@Test
	public void worksAsRegexWhenUsingParameters() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{dog.id}", converters, evaluator);
		assertThat(control.matches("/clients/15"), is(equalTo(true)));
	}

	@Test
	public void worksWithBasicRegexEvaluation() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients.*", converters, evaluator);
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

	@Test
	public void shouldTranslateAsteriskAsEmpty() {
		String uri = new DefaultParametersControl("/clients/.*", converters, evaluator).fillUri(new String[] {"client"}, client(3L));
		assertThat(uri, is(equalTo("/clients/")));
	}

    @Test
    public void shouldTranslatePatternArgs() {
        String uri = new DefaultParametersControl("/clients/{client.id}", converters, evaluator).fillUri(new String[] {"client"}, client(3L));
        assertThat(uri, is(equalTo("/clients/3")));
    }

    @Test
    public void shouldTranslatePatternArgsWithRegex() {
        String uri = new DefaultParametersControl("/clients/{id:[0-9]{1,}}", converters, evaluator).fillUri(new String[] {"id"}, 30L);
        assertThat(uri, is(equalTo("/clients/30")));
    }

    @Test
    public void shouldTranslatePatternArgsWithMultipleRegexes() {
        String uri = new DefaultParametersControl("/test/{hash1:[a-z0-9]{16}}{id}{hash2:[a-z0-9]{16}}/", converters, evaluator).fillUri(new String[] {"hash1", "id", "hash2"}, "0123456789abcdef", "1234", "fedcba9876543210");
        assertThat(uri, is(equalTo("/test/0123456789abcdef1234fedcba9876543210/")));
    }
    
	@Test
	public void shouldTranslatePatternArgNullAsEmpty() {
		String uri = new DefaultParametersControl("/clients/{client.id}", converters, evaluator).fillUri(new String[] {"client"}, client(null));
		assertThat(uri, is(equalTo("/clients/")));
	}

	@Test
	public void shouldUseConverterIfItExists() {
		when(converters.existsTwoWayFor(Client.class)).thenReturn(true);
		when(converters.twoWayConverterFor(Client.class)).thenReturn(converter);
		when(converter.convert(any(Client.class))).thenReturn("john");

		String uri = new DefaultParametersControl("/clients/{client}", converters, evaluator).fillUri(new String[] {"client"}, client(null));
		assertThat(uri, is(equalTo("/clients/john")));

	}

	@Test
	public void shouldTranslatePatternArgInternalNullAsEmpty() {
		String uri = new DefaultParametersControl("/clients/{client.child.id}", converters, evaluator) .fillUri(new String[] {"client"}, client(null));
		assertThat(uri, is(equalTo("/clients/")));
	}

	@Test
	public void shouldMatchPatternLazily() throws Exception {
		DefaultParametersControl wrong = new DefaultParametersControl("/clients/{client.id}/", converters, evaluator);
		DefaultParametersControl right = new DefaultParametersControl("/clients/{client.id}/subtask/", converters, evaluator);
		String uri = "/clients/3/subtask/";

		assertThat(wrong.matches(uri), is(false));
		assertThat(right.matches(uri), is(true));

	}

	@Test
	public void shouldMatchMoreThanOneVariable() throws Exception {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{client.id}/subtask/{task.id}/", converters, evaluator);

		assertThat(control.matches("/clients/3/subtask/5/"), is(true));
	}
	private Client client(Long id) {
		return new Client(id);
	}

	@Test
	public void shouldBeGreedyWhenIPutAnAsteriskOnExpression() throws Exception {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{pathToFile*}", converters, evaluator);

		assertThat(control.matches("/clients/my/path/to/file/"), is(true));
	}
	@Test
	public void shouldNotBeGreedyAtPatternCompiling() throws Exception {
		DefaultParametersControl control = new DefaultParametersControl("/project/{project.name}/build/{buildId}/view/{filename*}", converters, evaluator);

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
		DefaultParametersControl control = new DefaultParametersControl("/clients/{pathToFile*}", converters, evaluator);

		control.fillIntoRequest("/clients/my/path/to/file", request);

		verify(request).setParameter("pathToFile", new String[] {"my/path/to/file"});
	}

	@Test
	public void registerExtraParametersFromAcessedUrlWithGreedyAndDottedParameters() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{path.to.file*}", converters, evaluator);

		control.fillIntoRequest("/clients/my/path/to/file", request);

		verify(request).setParameter("path.to.file", new String[] {"my/path/to/file"});
	}

	static class PathToFile {

		public void withPath(String pathToFile) {

		}
	}
	@Test
	public void fillURLWithGreedyParameters() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{pathToFile*}", converters, evaluator);

		String filled = control.fillUri(new String[] {"pathToFile"}, "my/path/to/file");

		assertThat(filled, is("/clients/my/path/to/file"));
	}
	@Test
	public void fillURLWithoutGreedyParameters() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{pathToFile}", converters, evaluator);

		String filled = control.fillUri(new String[] {"pathToFile"}, "my/path/to/file");

		assertThat(filled, is("/clients/my/path/to/file"));
	}

	@Test
	public void whenNoParameterPatternsAreGivenShouldMatchAnything() throws Exception {
		ParametersControl control = new DefaultParametersControl("/any/{aParameter}/what", Collections.<String,String>emptyMap(), converters, evaluator);
		assertTrue(control.matches("/any/ICanPutAnythingInHere/what"));
	}
	@Test
	public void whenParameterPatternsAreGivenShouldMatchAccordingToGivenPatterns() throws Exception {
		ParametersControl control = new DefaultParametersControl("/any/{aParameter}/what",
				new ImmutableMap.Builder<String, String>().put("aParameter", "aaa\\d{3}bbb").build(), converters, evaluator);
		assertFalse(control.matches("/any/ICantPutAnythingInHere/what"));
		assertFalse(control.matches("/any/aaa12bbb/what"));
		assertTrue(control.matches("/any/aaa123bbb/what"));
	}

	@Test
	public void shouldFillRequestWhenAPatternIsSpecified() throws Exception {
		DefaultParametersControl control = new DefaultParametersControl("/project/{project.id}/build/",
				new ImmutableMap.Builder<String, String>().put("project.id", "\\d+").build(), converters, evaluator);

		String uri = "/project/15/build/";
		assertThat(control.matches(uri), is(true));

		control.fillIntoRequest(uri, request);

		verify(request).setParameter("project.id", "15");

		assertThat(control.apply(new String[] {"15"}),is(uri));
	}

	@Test
	public void shouldDecodeUriParameters() throws Exception {
		DefaultParametersControl control = new DefaultParametersControl("/clients/{name}", converters, evaluator);

		control.fillIntoRequest("/clients/Joao+Leno", request);

		verify(request).setParameter("name", "Joao Leno");

		control.fillIntoRequest("/clients/Paulo%20Macartinei", request);

		verify(request).setParameter("name", "Paulo Macartinei");
	}

}
