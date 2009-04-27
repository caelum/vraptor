package br.com.caelum.vraptor.view.jsp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

public class EmptyResultTest {
	@Test
	public void testIfOnlyWritesEnEmptyStringToResponse() throws IOException {
		Mockery mockery = new Mockery();
		mockery.setImposteriser(ClassImposteriser.INSTANCE);
		final HttpServletResponse response = mockery.mock(HttpServletResponse.class);
		final PrintWriter writer = mockery.mock(PrintWriter.class);
		mockery.checking(new Expectations() {{
				one(response).getWriter();
				will(returnValue(writer));
				one(writer).write("");
		}});

		new EmptyResult(response).doNothing();
		mockery.assertIsSatisfied();
	}
}
