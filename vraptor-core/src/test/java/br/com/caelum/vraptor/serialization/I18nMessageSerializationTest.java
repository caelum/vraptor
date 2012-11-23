package br.com.caelum.vraptor.serialization;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilder;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilderImpl;
import br.com.caelum.vraptor.serialization.xstream.XStreamJSONSerialization;
import br.com.caelum.vraptor.util.test.MockLocalization;
import br.com.caelum.vraptor.validator.SingletonResourceBundle;

public class I18nMessageSerializationTest {
	private I18nMessageSerialization serialization;
	private ByteArrayOutputStream stream;

    @Before
    public void setup() throws Exception {
    	stream = new ByteArrayOutputStream();
        
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(stream));
        DefaultTypeNameExtractor extractor = new DefaultTypeNameExtractor();
		HibernateProxyInitializer initializer = new HibernateProxyInitializer();
	    XStreamBuilder builder = XStreamBuilderImpl.cleanInstance();
		XStreamJSONSerialization jsonSerialization = new XStreamJSONSerialization(response, extractor, initializer, builder);
		
		MockLocalization mockLocalization = mock(MockLocalization.class);
		when(mockLocalization.getBundle()).thenReturn(new SingletonResourceBundle("message.cat", "Sweet"));

		serialization = new I18nMessageSerialization(jsonSerialization, mockLocalization);
    
    }
    
    @Test
    public void shouldCallXStreamJsonSerialization() {
    	String expectedResult = "{\"message\": \"Sweet\"}";
        serialization.from("message.cat").serialize();
        assertThat(result(), is(equalTo(expectedResult)));
    }

    @Test
    public void shouldCallXStreamJsonSerializationWithAlias() {
    	String expectedResult = "{\"message\": \"Sweet\"}";
    	serialization.from("message.cat", "message").serialize();
    	assertThat(result(), is(equalTo(expectedResult)));
    }
    
	private String result() {
		return new String(stream.toByteArray());
	}
	
}
