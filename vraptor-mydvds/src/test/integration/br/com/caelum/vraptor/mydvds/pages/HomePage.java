package br.com.caelum.vraptor.mydvds.pages;

import br.com.caelum.seleniumdsl.Browser;
import br.com.caelum.seleniumdsl.Form;

public class HomePage {

	private final Browser browser;
	private Form form;

	public HomePage(Browser browser) {
		this.browser = browser;
	}

	public HomePage fillRegisterDvdForm() {
		form = browser.currentPage().form("dvdRegister");
		return this;
	}

	public HomePage withTitle(String title) {
		form.field("dvd.title").type(title);
		return this;
	}

	public HomePage withDescription(String description) {
		form.field("dvd.description").type(description);
		return this;
	}

	public void andSend() {
		form.submit();
	}


}
