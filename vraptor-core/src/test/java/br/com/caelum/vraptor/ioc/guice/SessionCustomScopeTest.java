package br.com.caelum.vraptor.ioc.guice;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.util.Providers;

public class SessionCustomScopeTest {


	private static final String SESSION_ID = "abc";

	private SessionCustomScope scope;
	private @Mock HttpSession session;
	private @Mock LifecycleListener listener;



	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		scope = new SessionCustomScope();
		scope.setProvider(Providers.of(session));
		when(session.getId()).thenReturn(SESSION_ID);
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
