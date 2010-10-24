package br.com.caelum.vraptor.mydvds;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.When;

import br.com.caelum.vraptor.mydvds.pages.Home;
import br.com.caelum.vraptor.mydvds.pages.PageFactory;

public class MyDvdsSteps {

    private final PageFactory pageFactory;
    private Home home;

    public MyDvdsSteps(PageFactory pageFactory) {
        this.pageFactory = pageFactory;
    }

    @Given("user is unlogged")
    public void userIsOnHomePage(){        
        home = pageFactory.home();
        home.open();        
    }

    @When("user registers as \"$name\"")
    public void userRegisters(String name){        
		home.fillRegisterForm()
			.withLogin(name)
			.withName(name)
			.withPassword(name)
			.andSubmit();
    }

    @When("user tries to login as \"$name\"")
    public void userTriesToLoginAs(String name){        
		home.fillRegisterForm()
			.withLogin(name)
			.withName(name)
			.withPassword(name)
			.andSubmit();
    }

}
