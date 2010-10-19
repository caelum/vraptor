package br.com.caelum.vraptor.view;

import static br.com.caelum.vraptor.view.Results.logic;
import static br.com.caelum.vraptor.view.Results.page;

import org.jmock.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.http.route.ResourceNotFoundException;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.test.VRaptorMockery;

public class DefaultRefererResultTest {


	private VRaptorMockery mockery;
	private Result result;
	private MutableRequest request;
	private Router router;
	private DefaultRefererResult refererResult;
	private Localization localization;
	private ParametersProvider provider;

	@Before
	public void setUp() throws Exception {
		mockery = new VRaptorMockery();
		result = mockery.mock(Result.class);
		request = mockery.mock(MutableRequest.class);
		router = mockery.mock(Router.class);
		provider = mockery.mock(ParametersProvider.class);
		localization = mockery.mock(Localization.class);
		refererResult = new DefaultRefererResult(result, request, router, provider, localization);
	}

	@Test
	public void whenThereIsNoRefererShouldThrowExceptionOnForward() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(request).getHeader("Referer");
				will(returnValue(null));

				ignoring(anything());
			}
		});
		try {
			refererResult.forward();
			Assert.fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			mockery.assertIsSatisfied();
		}
	}
	@Test
	public void whenThereIsNoRefererShouldThrowExceptionOnRedirect() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(request).getHeader("Referer");
				will(returnValue(null));

				ignoring(anything());
			}
		});
		try {
			refererResult.redirect();
			Assert.fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			mockery.assertIsSatisfied();
		}
	}
	@Test
	public void whenRefererDontMatchAControllerShouldForwardToPage() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(request).getHeader("Referer");
				will(returnValue("http://localhost:8080/vraptor/no-controller"));

				one(request).getContextPath();
				will(returnValue("/vraptor"));

				one(router).parse("/no-controller", HttpMethod.GET, request);
				will(throwException(new ResourceNotFoundException()));

				PageResult page = mockery.mock(PageResult.class);

				one(result).use(page());
				will(returnValue(page));

				one(page).forwardTo("/no-controller");

				ignoring(anything());
			}
		});
		refererResult.forward();

		mockery.assertIsSatisfied();
	}
	@Test
	public void whenRefererDontMatchAControllerShouldRedirectToPage() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(request).getHeader("Referer");
				will(returnValue("http://localhost:8080/vraptor/no-controller"));

				one(request).getContextPath();
				will(returnValue("/vraptor"));

				one(router).parse("/no-controller", HttpMethod.GET, request);
				will(throwException(new ResourceNotFoundException()));

				PageResult page = mockery.mock(PageResult.class);

				one(result).use(page());
				will(returnValue(page));

				one(page).redirectTo("/no-controller");

				ignoring(anything());
			}
		});
		refererResult.redirect();

		mockery.assertIsSatisfied();
	}
	public static class RefererController {
		public void index() {

		}
	}
	@Test
	public void whenRefererMatchesAControllerShouldRedirectToIt() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(request).getHeader("Referer");
				will(returnValue("http://localhost:8080/vraptor/no-controller"));

				one(request).getContextPath();
				will(returnValue("/vraptor"));

				ResourceMethod method = mockery.methodFor(RefererController.class, "index");

				one(router).parse("/no-controller", HttpMethod.GET, request);
				will(returnValue(method));

				LogicResult logic = mockery.mock(LogicResult.class);

				one(result).use(logic());
				will(returnValue(logic));

				one(logic).redirectTo(RefererController.class);
				will(returnValue(new RefererController()));

				ignoring(anything());
			}
		});
		refererResult.redirect();

		mockery.assertIsSatisfied();
	}
	@Test
	public void whenRefererMatchesAControllerShouldForwardToIt() throws Exception {
		mockery.checking(new Expectations() {
			{
				one(request).getHeader("Referer");
				will(returnValue("http://localhost:8080/vraptor/no-controller"));

				one(request).getContextPath();
				will(returnValue("/vraptor"));

				ResourceMethod method = mockery.methodFor(RefererController.class, "index");

				one(router).parse("/no-controller", HttpMethod.GET, request);
				will(returnValue(method));

				LogicResult logic = mockery.mock(LogicResult.class);

				one(result).use(logic());
				will(returnValue(logic));

				one(logic).forwardTo(RefererController.class);
				will(returnValue(new RefererController()));

				ignoring(anything());
			}
		});
		refererResult.forward();

		mockery.assertIsSatisfied();
	}
}
