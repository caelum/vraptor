package br.com.caelum.vraptor.util.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class MockHttpServletResponseTest {


	private MockHttpServletResponse response;

	@Before
	public void setUp() throws Exception {
		response = new MockHttpServletResponse();
	}

	@Test 
	public void shouldBeAbleToReturnContentIntoWriteAsString() throws IOException {
			response.getWriter().write("X");
			response.getWriter().flush();
			assertEquals("X", response.getContentAsString());
	}
	
	@Test
	public void shouldBeAbleToReturnResponseStatusCode() {
		response.setStatus(401);
		assertEquals(401, response.getStatus());
	}
}
