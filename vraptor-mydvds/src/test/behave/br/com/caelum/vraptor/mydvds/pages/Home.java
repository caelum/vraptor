package br.com.caelum.vraptor.mydvds.pages;

import org.jbehave.web.selenium.WebDriverFactory;
import org.openqa.selenium.By;

import java.util.concurrent.TimeUnit;

public class Home extends TraderPage {

    public Home(WebDriverFactory driverFactory) {
        super(driverFactory);
    }

    public void open() {
        get("http://localhost:8080/vraptor-mydvds/");
        manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    public FindSteps findSteps(PageFactory factory){
        findElement(By.linkText("Find Steps")).click();
        return factory.findSteps();
    }
    
    public RunStory runStory(PageFactory factory){
        findElement(By.linkText("Run Story")).click();
        return factory.runStory();
    }
}
