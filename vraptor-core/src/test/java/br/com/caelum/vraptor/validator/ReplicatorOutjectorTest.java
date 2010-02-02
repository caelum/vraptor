package br.com.caelum.vraptor.validator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.resource.ResourceMethod;

public class ReplicatorOutjectorTest {


	private @Mock ParameterNameProvider provider;
	private @Mock MethodInfo method;
	private @Mock Result result;
	private @Mock ResourceMethod resourceMethod;

	private Outjector outjector;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(method.getResourceMethod()).thenReturn(resourceMethod);
		outjector = new ReplicatorOutjector(result, method, provider);
	}

	@Test
	public void shouldReplicateMethodParametersToNextRequest() throws Exception {
		when(provider.parameterNamesFor(any(Method.class))).thenReturn(new String[] {"first", "second", "third"});
		when(method.getParameters()).thenReturn(new Object[] {1, 2.0, 3l});

		outjector.outjectRequestMap();

		verify(result).include("first", 1);
		verify(result).include("second", 2.0);
		verify(result).include("third", 3l);
	}
}
