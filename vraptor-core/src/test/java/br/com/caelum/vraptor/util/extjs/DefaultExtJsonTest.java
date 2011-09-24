package br.com.caelum.vraptor.util.extjs;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.NullProxyInitializer;

public class DefaultExtJsonTest {


	private DefaultExtJson fixedExtJson;
	private @Mock HttpServletResponse response;
	private @Mock TypeNameExtractor extractor;
	private StringWriter writer;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		writer = new StringWriter();
		when(response.getWriter()).thenReturn(new PrintWriter(writer));
		when(extractor.nameFor(Data.class)).thenReturn("data");
		fixedExtJson = new DefaultExtJson(response, extractor, new NullProxyInitializer());
	}

	@Test
	public void shouldSerializeWithData() throws Exception {
		Data data = new Data();
		data.test = "testing";
		fixedExtJson.from(data).serialize();

		assertThat(writer.toString(), is("{\"data\": {\"test\": \"testing\"}}"));
	}
	@Test
	public void shouldSerializeWithListsOfData() throws Exception {
		Data data1 = new Data();
		data1.test = "testing";
		Data data2 = new Data();
		data2.test = "testing2";
		fixedExtJson.from(Arrays.asList(data1, data2)).serialize();

		assertThat(writer.toString(), is("{\"data\": [{\"test\": \"testing\"},{\"test\": \"testing2\"}]}"));
	}
	@Test
	public void shouldExcludeFieldsOnData() throws Exception {
		Data data1 = new Data();
		data1.test = "testing";
		fixedExtJson.from(data1).exclude("test").serialize();

		assertThat(writer.toString(), is("{\"data\": {}}"));
	}
	@Test
	public void shouldExcludeWithListsOfData() throws Exception {
		Data data1 = new Data();
		data1.test = "testing";
		Data data2 = new Data();
		data2.test = "testing2";
		fixedExtJson.from(Arrays.asList(data1, data2)).exclude("test").serialize();

		assertThat(writer.toString(), is("{\"data\": [{},{}]}"));
	}
}
