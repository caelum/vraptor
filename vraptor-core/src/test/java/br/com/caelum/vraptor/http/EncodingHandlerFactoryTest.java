package br.com.caelum.vraptor.http;

import static br.com.caelum.vraptor.config.BasicConfiguration.ENCODING;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;

public class EncodingHandlerFactoryTest {

    private ServletContext context;

    @Before
    public void setUp() throws Exception {
        context = mock(ServletContext.class);
    }

    @Test
    public void shouldReturnANullHandlerWhenThereIsNoEncodingInitParameter() throws Exception {
        when(context.getInitParameter(ENCODING)).thenReturn(null);
        
        EncodingHandlerFactory handlerFactory = new EncodingHandlerFactory(context);
        assertThat(handlerFactory.getInstance(), is(instanceOf(NullEncodingHandler.class)));
    }
    @Test
    public void shouldReturnAWebXmlHandlerWhenThereIsAnEncodingInitParameter() throws Exception {
        when(context.getInitParameter(ENCODING)).thenReturn("UTF-8");
        
        EncodingHandlerFactory handlerFactory = new EncodingHandlerFactory(context);
        assertThat(handlerFactory.getInstance(), is(instanceOf(WebXmlEncodingHandler.class)));
    }
}
