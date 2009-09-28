package br.com.caelum.vraptor.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.proxy.ObjenesisProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class DefaultHttpResultTest {


	private VRaptorMockery mockery;
	private DefaultHttpResult httpResult;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Router router;
	private Proxifier proxifier;

	@Before
	public void setUp() throws Exception {
		mockery = new VRaptorMockery();

		request = mockery.mock(HttpServletRequest.class);
		response = mockery.mock(HttpServletResponse.class);
		router = mockery.mock(Router.class);
		proxifier = new ObjenesisProxifier();

		httpResult = new DefaultHttpResult(request, response, router, proxifier);
	}

	@Test
	public void shouldMoveToAbsoluteUriUsingContextPath() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(request).getContextPath();
				will(returnValue("/context"));

				one(response).setHeader("Location", "/context/my/uri");
				one(response).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
				ignoring(response);
			}
		});
		httpResult.movedPermanentlyTo("/my/uri");
	}
	@Test
	public void shouldMoveToExactlyURIWhenItIsNotAbsolute() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(response).setHeader("Location", "http://www.caelum.com.br");
				one(response).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
				ignoring(response);
			}
		});
		httpResult.movedPermanentlyTo("http://www.caelum.com.br");
	}

	public class RandomController {
		public void method() {
		}
	}
	@Test
	public void shouldMoveToURIFromController() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(router).urlFor(RandomController.class, RandomController.class.getDeclaredMethods()[0]);
				will(returnValue("/my/random/uri"));
				one(request).getContextPath();
				will(returnValue("/context"));

				one(response).setHeader("Location", "/context/my/random/uri");
				one(response).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
				ignoring(response);
			}
		});
		httpResult.movedPermanentlyTo(RandomController.class).method();
	}

}
