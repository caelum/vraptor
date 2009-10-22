/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.mydvds;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;

import br.com.caelum.seleniumdsl.Browser;
import br.com.caelum.seleniumdsl.htmlunit.HtmlUnitBrowser;
import br.com.caelum.vraptor.mydvds.pages.HomePage;
import br.com.caelum.vraptor.mydvds.pages.RootPage;

/**
 * Base class for integration tests.
 * It uses SeleniumDSL with HtmlUnit for simulating the browser using the system.
 *
 * You'll need a MyDVDs running on a server on the port 8080 and context vraptor-mydvds
 *
 * @author Lucas Cavalcanti
 *
 */
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
		assertTrue("There is no erros", browser.currentPage().div("errors").exists());
	}

	public void assertContainsMessage(String message) {
		assertThat(browser.currentPage().div("notice").innerHTML(), containsString(message));
	}

	public void thereIsAUserNamed(String name) {
		openRootPage().fillRegisterForm()
			.withLogin(name)
			.withName(name)
			.withPassword(name)
			.andSubmit();
	}

	public void assertLoggedUserIs(String name) {
		assertThat(browser.currentPage().div("userInfo").innerHTML(), containsString(name));
	}

	public HomePage loginAs(String name) {
		thereIsAUserNamed(name);
		openRootPage().fillLoginForm()
			.withLogin(name)
			.withPassword(name)
			.andSubmit();
		return new HomePage(browser);
	}
}
