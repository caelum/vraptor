package br.com.caelum.vraptor.mydvds;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;

import br.com.caelum.seleniumdsl.Browser;
import br.com.caelum.seleniumdsl.htmlunit.HtmlUnitBrowser;
import br.com.caelum.vraptor.mydvds.pages.RootPage;

public abstract class IntegrationTestCase {

	protected Browser browser;

	@Before
	public void setUp() throws Exception {
		browser = new HtmlUnitBrowser("http://localhost:8080/vraptor-mydvds");
	}

	public RootPage openRootPage() {
		browser.open("/");
		return new RootPage(browser);
	}

	public void assertContainsErrors() {
		assertTrue("There is no erros", browser.currentPage().div("error-box").exists());
	}

	public void assertContainsMessage(String message) {
		assertThat(browser.currentPage().div("site").innerHTML(), containsString(message));
	}

	public void thereIsAUserNamed(String name) {
		openRootPage().fillRegisterForm()
			.withLogin(name)
			.withName(name)
			.withPassword(name)
			.andSubmit();
	}

	public void assertLoggedUserIs(String name) {
		assertThat(browser.currentPage().div("user").innerHTML(), containsString(name));
	}
}
