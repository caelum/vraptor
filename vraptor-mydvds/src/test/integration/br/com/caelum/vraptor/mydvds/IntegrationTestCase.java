package br.com.caelum.vraptor.mydvds;

import org.junit.Before;

import br.com.caelum.seleniumdsl.Browser;
import br.com.caelum.seleniumdsl.htmlunit.HtmlUnitBrowser;

public abstract class IntegrationTestCase {

	protected Browser browser;

	@Before
	public void setUp() throws Exception {
		browser = new HtmlUnitBrowser("http://localhost:8080/vraptor-mydvds");
	}

	public void openRootPage() {
		browser.open("/");
	}

}
