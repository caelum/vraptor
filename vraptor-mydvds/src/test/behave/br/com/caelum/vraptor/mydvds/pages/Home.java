package br.com.caelum.vraptor.mydvds.pages;

import java.util.concurrent.TimeUnit;

import org.jbehave.web.runner.wicket.pages.RunStory;
import org.jbehave.web.selenium.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import br.com.caelum.seleniumdsl.Browser;
import br.com.caelum.seleniumdsl.Form;

public class Home extends DefaultPage {

    public Home(WebDriverFactory driverFactory) {
        super(driverFactory);
    }

    public void open() {
        get("http://localhost:8080/vraptor-mydvds/");
        manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

	private WebElement form;
	private String prefix;

	public Home fillLoginForm() {
		form = findElement(By.id("loginForm"));
		prefix = "";
		return this;
	}
	
	public Home fillRegisterForm() {
		form = findElement(By.id("registerForm"));
		prefix = "user.";
		return this;
	}

	public Home withName(String name) {
		form.findElement(By.name("user.name")).sendKeys(name);
		return this;
	}
	public Home withLogin(String login) {
		form.findElement(By.name(prefix + "login")).sendKeys(login);
		return this;
	}
	public Home withPassword(String password) {
		form.findElement(By.name(prefix + "password")).sendKeys(password);
		return this;
	}
	public void andSubmit() {
		form.submit();
	}

}
