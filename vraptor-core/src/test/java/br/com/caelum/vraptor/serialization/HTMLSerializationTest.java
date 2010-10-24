package br.com.caelum.vraptor.serialization;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.view.PageResult;

public class HTMLSerializationTest {


	private @Mock Result result;
	private @Mock PageResult pageResult;
	private @Mock TypeNameExtractor extractor;

	private HTMLSerialization serialization;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(result.use(PageResult.class)).thenReturn(pageResult);

		serialization = new HTMLSerialization(result, extractor);
	}

	@Test
	public void shouldForwardToDefaultViewWithoutAlias() throws Exception {
		serialization.from(new Object());
		verify(pageResult).defaultView();
	}
	@Test
	public void shouldForwardToDefaultViewWithAlias() throws Exception {
		serialization.from(new Object(), "Abc");
		verify(pageResult).defaultView();
	}
	@Test
	public void shouldIncludeOnResultWithoutAlias() throws Exception {
		Object object = new Object();
		when(extractor.nameFor(Object.class)).thenReturn("obj");

		serialization.from(object);

		verify(result).include("obj", object);
	}
	@Test
	public void shouldIncludeOnResultWithAlias() throws Exception {
		Object object = new Object();

		serialization.from(object, "Abc");

		verify(result).include("Abc", object);
	}
	@Test
	public void shouldReturnAnIgnoringSerializerWithoutAlias() throws Exception {
		Serializer serializer = serialization.from(new Object());
		assertThat(serializer, is(instanceOf(IgnoringSerializer.class)));
	}

	@Test
	public void shouldReturnAnIgnoringSerializerWithAlias() throws Exception {
		Serializer serializer = serialization.from(new Object(), "Abc");
		assertThat(serializer, is(instanceOf(IgnoringSerializer.class)));
	}
}
