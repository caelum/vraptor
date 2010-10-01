package br.com.caelum.vraptor.mydvds;

import java.io.FileOutputStream;

import org.apache.commons.io.IOUtils;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.AfterScenario.Outcome;
import org.jbehave.web.selenium.PerScenarioWebDriverSteps;
import org.jbehave.web.selenium.WebDriverFactory;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import sun.rmi.rmic.iiop.Generator.OutputType;
import org.openqa.selenium.OutputType;

public class FailingScenarioScreenshotCapture  extends PerScenarioWebDriverSteps  {
    public FailingScenarioScreenshotCapture(WebDriverFactory driverFactory) {
        super(driverFactory);
    }

    @AfterScenario(uponOutcome = Outcome.FAILURE)
    public void afterScenarioFailure() throws Exception {
        WebDriver webDriver = driverFactory.get();
        if (webDriver instanceof TakesScreenshot) {
            byte[] bytes = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
            String path = System.getenv("HOME") + "/failed-scenario-" + System.currentTimeMillis() + ".png";
            IOUtils.write(bytes, new FileOutputStream(path));
            System.out.println("Screenshot at: " + path);
        } else {
            System.out.println("Screenshot cannot be taken: driver " + webDriver.getClass().getName() + " does not support screenshooting");
        }
        super.afterScenario();
    }

    @Override
    @AfterScenario(uponOutcome = Outcome.SUCCESS)
    public void afterScenario() throws Exception {
        super.afterScenario();
    }


}
