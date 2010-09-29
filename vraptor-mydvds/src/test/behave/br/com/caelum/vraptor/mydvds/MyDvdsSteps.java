package br.com.caelum.vraptor.mydvds;

import java.util.List;

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.web.examples.trader.webdriver.pages.FindSteps;
import org.jbehave.web.examples.trader.webdriver.pages.Home;
import org.jbehave.web.examples.trader.webdriver.pages.PageFactory;
import org.jbehave.web.examples.trader.webdriver.pages.RunStory;

public class MyDvdsSteps {

    private final PageFactory pageFactory;
    private Home home;
    private FindSteps findSteps;
    private RunStory runStory;

    public TraderWebSteps(PageFactory pageFactory) {
        this.pageFactory = pageFactory;
    }

    @Given("user is on home page")
    public void userIsOnHomePage(){        
        home = pageFactory.home();
        home.open();        
    }

    @When("user clicks on Find Steps")
    public void userClicksOnFindSteps(){        
        findSteps = home.findSteps(pageFactory);
    }

    @When("user clicks on Run Story")
    public void userClicksOnRunStory(){        
        runStory = home.runStory(pageFactory);
    }

    @When("user searches for \"$step\"")
    public void userSearchesForSteps(String step){        
        findSteps.find(step);
    }

    @When("user searches for all steps")
    public void userSearchesAllSteps(){        
        findSteps.find("");
    }

    @When("user views with methods")
    public void userViewWithMethods(){
        findSteps.viewWithMethods();
    }

    @When("user sorts by pattern")
    public void userSortsByPattern(){
        findSteps.sortByPattern();
    }

    @When("user runs story \"$story\"")
    public void userRunsStory(String story){        
        runStory.run(story);
    }

    @Then("run is successful")
    public void runIsSuccessful(){        
        runStory.runIsSuccessful();
    }

    @Then("text is shown: \"$text\"")
    public void textIsPresent(String text){   
        findSteps.found(text);
    }

    @Then("search returns: \"$stepsOrMethods\"")
    public void stepsFound(List<String> stepsOrMethods){   
        findSteps.found(stepsOrMethods);
    }

    @Then("steps instances include: \"$names\"")
    public void stepsInstancesFound(List<String> names){   
        findSteps.found(names);
    }
}
