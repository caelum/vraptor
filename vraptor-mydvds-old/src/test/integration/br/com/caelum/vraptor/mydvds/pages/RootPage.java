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
package br.com.caelum.vraptor.mydvds.pages;

import br.com.caelum.seleniumdsl.Browser;
import br.com.caelum.seleniumdsl.Form;

/**
 * Page object that represents root page
 *
 * @author Lucas Cavalcanti
 *
 */
public class RootPage {

	private final Browser browser;
	private Form form;
	private String prefix;

	public RootPage(Browser browser) {
		this.browser = browser;
	}

	public RootPage fillRegisterForm() {
		form = browser.currentPage().form("registerForm");
		prefix = "user.";
		return this;
	}
	public RootPage fillLoginForm() {
		form = browser.currentPage().form("loginForm");
		prefix = "";
		return this;
	}

	public RootPage withName(String name) {
		form.field("user.name").type(name);
		return this;
	}
	public RootPage withLogin(String login) {
		form.field(prefix + "login").type(login);
		return this;
	}
	public RootPage withPassword(String password) {
		form.field(prefix + "password").type(password);
		return this;
	}
	public void andSubmit() {
		form.navigate(prefix + "submit");
	}
}
