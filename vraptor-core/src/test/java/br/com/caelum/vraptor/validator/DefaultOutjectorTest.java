package br.com.caelum.vraptor.validator;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class DefaultOutjectorTest {

	private RequestAttributes request;
	private DefaultOutjector outjector;

	static class RequestAttributes extends HttpServletRequestWrapper {
		private Map<String, Object> attributes = new HashMap<String, Object>();
		private Map<String, String[]> parameters = new HashMap<String, String[]>();

		public RequestAttributes(HttpServletRequest request) {
			super(request);
		}

		@Override
		public Object getAttribute(String name) {
			return attributes.get(name);
		}

		@Override
		public void setAttribute(String name, Object o) {
			attributes.put(name, o);
		}

		public void setParameter(String name, String value) {
			parameters.put(name, new String[] { value });
		}

		@Override
		public String getParameter(String name) {
		    String[] s = parameters.get(name);
			return s == null || s.length == 0 ? null : s[0];
		}
		@Override
		public Map<String, String[]> getParameterMap() {
			return parameters;
		}
	}
	@Before
	public void setUp() throws Exception {
		request = new RequestAttributes(mock(HttpServletRequest.class));
		outjector = new DefaultOutjector(request);
	}

	@Test
	public void shouldNotThrowExceptions() throws Exception {

		givenParameterIsPresent("simple","anything");
		givenParameterIsPresent("with.dots", "anything");
		givenParameterIsPresent("with.dots.again", "anything");
		givenParameterIsPresent("an[0].array", "anything");

		outjector.outjectRequestMap();

	}

	@Test
	public void shouldOutjectSimpleParameters() throws Exception {

		givenParameterIsPresent("simple", "anything");

		outjector.outjectRequestMap();

		assertThat(request.getAttribute("simple"), is("anything"));
	}

	@Test
	public void shouldOutjectParametersWithDot() throws Exception {

		givenParameterIsPresent("with.dot", "value");

		outjector.outjectRequestMap();

		Map<?, ?> with = castMap(request.getAttribute("with"));

		assertThat(with.get("dot"), is("value"));
	}

	@Test
	public void shouldOutjectParametersWithSeveralDots() throws Exception {

		givenParameterIsPresent("with.several.dots", "value");

		outjector.outjectRequestMap();

		Map<?,?> with = castMap(request.getAttribute("with"));

		Map<?,?> several = castMap(with.get("several"));

		assertThat(several.get("dots"), is("value"));
	}
	@Test
	public void shouldOutjectIndexedParameters() throws Exception {

		givenParameterIsPresent("indexed[0]", "value");

		outjector.outjectRequestMap();

		Map<?,?> indexed = castMap(request.getAttribute("indexed"));

		assertThat(indexed.get(0L), is("value"));
	}

	@Test
	public void shouldOutjectIndexedParametersWithDots() throws Exception {

		givenParameterIsPresent("indexed[0].dots", "value");

		outjector.outjectRequestMap();

		Map<?,?> indexed = castMap(request.getAttribute("indexed"));

		Map<?,?> zero = castMap(indexed.get(0L));

		assertThat(zero.get("dots"), is("value"));
	}

	@Test
	public void shouldOutjectSeveralParametersWithDots() throws Exception {

		givenParameterIsPresent("with.dot", "value");
		givenParameterIsPresent("with.other", "abc");

		outjector.outjectRequestMap();

		Map<?, ?> with = castMap(request.getAttribute("with"));

		assertThat(with.get("dot"), is("value"));
		assertThat(with.get("other"), is("abc"));
	}

	private Matcher<? super Object> is(Object object) {
		return Matchers.is(object);
	}

	private Map<?, ?> castMap(Object map) {
		assertTrue("should be an instance of Map, but was " + map,map instanceof Map<?,?>);
		return ((Map<?, ?>) map);
	}

	private void givenParameterIsPresent(String key, String value) {
		request.setParameter(key, value);
	}
}
