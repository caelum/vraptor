package br.com.caelum.vraptor.util.test;

import static br.com.caelum.vraptor.view.Results.http;
import static br.com.caelum.vraptor.view.Results.json;
import static br.com.caelum.vraptor.view.Results.logic;
import static br.com.caelum.vraptor.view.Results.page;
import static br.com.caelum.vraptor.view.Results.representation;
import static br.com.caelum.vraptor.view.Results.status;
import static br.com.caelum.vraptor.view.Results.xml;

import org.junit.Before;
import org.junit.Test;

public class MockResultTest {


	private MockResult result;

	@Before
	public void setUp() throws Exception {
		result = new MockResult();
	}

	static class AController {
		void aMethod() {}
	}
	@Test
	public void shouldNotThrowNullPointersOnLogicResult() throws Exception {
		result.use(logic()).forwardTo(AController.class).aMethod();
		result.use(logic()).redirectTo(AController.class).aMethod();
	}

	@Test
	public void shouldNotThrowNullPointersOnPageResult() throws Exception {
		result.use(page()).defaultView();
		result.use(page()).forwardTo("Abc");
		result.use(page()).include();
		result.use(page()).of(AController.class).aMethod();
		result.use(page()).redirectTo("abc");
	}
	@Test
	public void shouldNotThrowNullPointersOnHttpResult() throws Exception {
		result.use(http())
			.addDateHeader(null, 0)
			.addHeader(null, null)
			.addIntHeader(null, 0);
		result.use(http()).sendError(0);
		result.use(http()).sendError(0, "");
		result.use(http()).setStatusCode(0);
	}
	@Test
	public void shouldNotThrowNullPointersOnJsonResult() throws Exception {
		result.use(json()).withoutRoot().from("abc").exclude("aaa").include("aaa").serialize();
	}
	@Test
	public void shouldNotThrowNullPointersOnXmlResult() throws Exception {
		result.use(xml()).from("abc").exclude("aaa").include("aaa").serialize();
	}
	@Test
	public void shouldNotThrowNullPointersOnRepresentationResult() throws Exception {
		result.use(representation()).from("abc").exclude("aaa").include("aaa").serialize();
	}
	@Test
	public void shouldNotThrowNullPointersOnStatusResult() throws Exception {
		result.use(status()).movedPermanentlyTo(AController.class).aMethod();
	}
}
