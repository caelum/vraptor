package br.com.caelum.vraptor.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class DefaultMethodInfoTest {
	private final MethodInfo methodInfo = new DefaultMethodInfo();

	@Test
	public void unsetParameters() throws Exception {
		assertFalse(methodInfo.parametersWereSet());
	}

	@Test
	public void setParameters() throws Exception {
		methodInfo.setParameters(new Object[] {"a", "b"});
		assertTrue(methodInfo.parametersWereSet());
	}
}
