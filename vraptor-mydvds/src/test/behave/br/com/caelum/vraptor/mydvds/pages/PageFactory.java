package br.com.caelum.vraptor.mydvds.pages;

import org.jbehave.web.runner.wicket.pages.RunStory;
import org.jbehave.web.selenium.WebDriverFactory;

public class PageFactory {

    private final WebDriverFactory driverFactory;

    public PageFactory(WebDriverFactory driverFactory) {
        this.driverFactory = driverFactory;
    }

    public Home home(){
        return new Home(driverFactory);
    }

}
