package br.com.caelum.vraptor.view;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.http.route.Router;

public class DefaultRefererResultTest {


	private Mockery mockery;
	private Result result;
	private MutableRequest request;
	private Router router;
	private DefaultRefererResult refererResult;
	private Localization localization;
	private ParametersProvider provider;

	@Before
	public void setUp() throws Exception {
		mockery = new Mockery();
		result = mockery.mock(Result.class);
		request = mockery.mock(MutableRequest.class);
		router = mockery.mock(Router.class);
		provider = mockery.mock(ParametersProvider.class);
		localization = mockery.mock(Localization.class);
		refererResult = new DefaultRefererResult(result, request, router, provider, localization);
	}

	@Test
	public void whenThereIsNoRefererShouldFallbackToGivenResult() throws Exception {
		mockery.checking(new Expectations() {
			{
				exactly(2).of(request).getHeader("Referer");
				will(returnValue(null));

				exactly(2).of(result).use(View.class);

				ignoring(anything());
			}
		});
		refererResult.forward().or(View.class);
		refererResult.redirect().or(View.class);

		mockery.assertIsSatisfied();
	}
}
