package br.com.caelum.vraptor.validator;

import javax.servlet.http.HttpServletRequest;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.view.RequestOutjectMap;

import com.google.common.collect.ImmutableMap;

public class DefaultOutjectorTest {

	private Mockery mockery;
	private HttpServletRequest request;
	private DefaultOutjector outjector;

	@Before
	public void setUp() throws Exception {
		mockery = new Mockery();
		request = mockery.mock(HttpServletRequest.class);

		outjector = new DefaultOutjector(request);
	}

	@Test
	public void shouldIncludeMagicMaps() throws Exception {

		mockery.checking(new Expectations() {
			{
				one(request).getParameterMap();
				will(returnValue(ImmutableMap.of(
						"simple","anything",
						"with.dots", "anything",
						"with.dots.again", "anything",
						"an[0].array", "anything"
					)));

				one(request).getAttribute("simple"); will(returnValue(null));
				one(request).getAttribute("with"); will(returnValue(null));
				one(request).getAttribute("with"); will(returnValue("anything"));
				one(request).getAttribute("an"); will(returnValue(null));

				one(request).setAttribute(with(equal("simple")), with(any(RequestOutjectMap.class)));
				one(request).setAttribute(with(equal("with")), with(any(RequestOutjectMap.class)));
				one(request).setAttribute(with(equal("an")), with(any(RequestOutjectMap.class)));

			}
		});

		outjector.outjectRequestMap();

		mockery.assertIsSatisfied();
	}
}
