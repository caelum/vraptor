package br.com.caelum.vraptor.ioc.guice;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.ioc.spring.VRaptorRequestHolder;

public class SessionCustomScopeTest {


	private static final String SESSION_ID = "abc";

	private SessionCustomScope scope;
	private @Mock HttpSession session;
	private @Mock MutableRequest request;
	private @Mock LifecycleListener listener;



	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		scope = new SessionCustomScope();
		when(session.getId()).thenReturn(SESSION_ID);
		when(request.getSession()).thenReturn(session);
		VRaptorRequestHolder.setRequestForCurrentThread(new RequestInfo(null, null, request, null));
	}

	@Test
	public void shouldClearAllListenersFromSessionOnStart() throws Exception {
		scope.registerDestroyListener(listener);
		verifyNoMoreInteractions(listener);

		scope.start(session);
		scope.stop(session);
	}

	@Test
	public void shouldInvokeListenersOnStop() throws Exception {
		scope.registerDestroyListener(listener);

		scope.stop(session);

		verify(listener).onEvent();
	}
	@Test
	public void shouldRemoveListenersOnStop() throws Exception {
		scope.registerDestroyListener(listener);

		scope.stop(session);
		scope.stop(session);

		verify(listener).onEvent();

	}
}
