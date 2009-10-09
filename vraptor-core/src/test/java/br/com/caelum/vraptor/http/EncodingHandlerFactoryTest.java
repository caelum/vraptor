package br.com.caelum.vraptor.http;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.servlet.ServletContext;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.config.BasicConfiguration;

public class EncodingHandlerFactoryTest {


	private Mockery mockery;
	private ServletContext context;

	@Before
	public void setUp() throws Exception {
		mockery = new Mockery();
		context = mockery.mock(ServletContext.class);

	}

	@Test
	public void shouldReturnANullHandlerWhenThereIsNoEncodingInitParameter() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(context).getInitParameter(BasicConfiguration.ENCODING);
				will(returnValue(null));
			}
		});
		EncodingHandlerFactory handlerFactory = new EncodingHandlerFactory(context);
		assertThat(handlerFactory.getInstance(), is(instanceOf(NullEncodingHandler.class)));
	}
	@Test
	public void shouldReturnAWebXmlHandlerWhenThereIsAnEncodingInitParameter() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(context).getInitParameter(BasicConfiguration.ENCODING);
				will(returnValue("UTF-8"));
			}
		});
		EncodingHandlerFactory handlerFactory = new EncodingHandlerFactory(context);
		assertThat(handlerFactory.getInstance(), is(instanceOf(WebXmlEncodingHandler.class)));
	}
}
