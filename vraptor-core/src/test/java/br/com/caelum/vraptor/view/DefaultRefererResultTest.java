package br.com.caelum.vraptor.view;

import static br.com.caelum.vraptor.view.Results.logic;
import static br.com.caelum.vraptor.view.Results.page;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.http.route.ResourceNotFoundException;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class DefaultRefererResultTest {

	private @Mock Result result;
	private @Mock MutableRequest request;
	private @Mock Router router;
	private @Mock Localization localization;
	private @Mock ParametersProvider provider;
	private DefaultRefererResult refererResult;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		refererResult = new DefaultRefererResult(result, request, router, provider, localization);
	}

	@Test
	public void whenThereIsNoRefererShouldThrowExceptionOnForward() throws Exception {
		when(request.getHeader("Referer")).thenReturn(null);
		
		try {
			refererResult.forward();
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			
		}
	}
	
	@Test
	public void whenThereIsNoRefererShouldThrowExceptionOnRedirect() throws Exception {
		when(request.getHeader("Referer")).thenReturn(null);

		try {
			refererResult.redirect();
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			
		}
	}
	
	@Test
	public void whenRefererDontMatchAControllerShouldForwardToPage() throws Exception {
		PageResult page = mock(PageResult.class);
		
		when(request.getHeader("Referer")).thenReturn("http://localhost:8080/vraptor/no-controller");
		when(request.getContextPath()).thenReturn("/vraptor");
		when(router.parse("/no-controller", HttpMethod.GET, request)).thenThrow(new ResourceNotFoundException());
		doReturn(page).when(result).use(page());

		refererResult.forward();
		
		verify(page).forwardTo("/no-controller");
	}
	
	@Test
	public void whenRefererDontMatchAControllerShouldRedirectToPage() throws Exception {
		PageResult page = mock(PageResult.class);
		
		when(request.getHeader("Referer")).thenReturn("http://localhost:8080/vraptor/no-controller");
		when(request.getContextPath()).thenReturn("/vraptor");
		when(router.parse("/no-controller", HttpMethod.GET, request)).thenThrow(new ResourceNotFoundException());
		doReturn(page).when(result).use(page());
		
		refererResult.redirect();
		
		verify(page).redirectTo("/no-controller");
	}
	
	public static class RefererController {
		public void index() {

		}
	}
	
	@Test
	public void whenRefererMatchesAControllerShouldRedirectToIt() throws Exception {
		LogicResult logic = mock(LogicResult.class);

		Method index = RefererController.class.getMethod("index");
		ResourceMethod method = DefaultResourceMethod.instanceFor(RefererController.class, index);

		when(request.getHeader("Referer")).thenReturn("http://localhost:8080/vraptor/no-controller");
		when(request.getContextPath()).thenReturn("/vraptor");
		when(router.parse("/no-controller", HttpMethod.GET, request)).thenReturn(method);
		doReturn(logic).when(result).use(logic());
		when(logic.redirectTo(RefererController.class)).thenReturn(new RefererController());

		refererResult.redirect();
	}
	@Test
	public void whenRefererMatchesAControllerShouldForwardToIt() throws Exception {
		LogicResult logic = mock(LogicResult.class);
		
		Method index = RefererController.class.getMethod("index");
		ResourceMethod method = DefaultResourceMethod.instanceFor(RefererController.class, index);
		
		when(request.getHeader("Referer")).thenReturn("http://localhost:8080/vraptor/no-controller");
		when(request.getContextPath()).thenReturn("/vraptor");
		when(router.parse("/no-controller", HttpMethod.GET, request)).thenReturn(method);
		doReturn(logic).when(result).use(logic());
		when(logic.forwardTo(RefererController.class)).thenReturn(new RefererController());
		
		refererResult.forward();
	}
}
