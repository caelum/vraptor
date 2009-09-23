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
		form.submit();
	}
}
