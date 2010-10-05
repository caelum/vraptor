package br.com.caelum.vraptor.mydvds;

import static org.jbehave.core.reporters.StoryReporterBuilder.Format.IDE_CONSOLE;

import org.jbehave.core.reporters.ConsoleOutput;
import org.jbehave.core.reporters.StoryReporter;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.web.selenium.ContextView;
import org.jbehave.web.selenium.SeleniumContext;

public class MyBuilder extends StoryReporterBuilder {

	private final SeleniumContext context;
	private final ContextView contextView;

	public MyBuilder(SeleniumContext context, ContextView contextView) {
		this.context = context;
		this.contextView = contextView;
	}

	@Override
	public StoryReporter reporterFor(String storyPath, Format format) {
		if (format == IDE_CONSOLE) {
			return new ConsoleOutput() {
				@Override
				public void beforeScenario(String scenarioTitle) {
					context.setCurrentScenario(scenarioTitle);
					super.beforeScenario(scenarioTitle);
				}

				@Override
				public void afterStory(boolean givenStory) {
					contextView.close();
					super.afterStory(givenStory);
				}
			};
		} else {
			return super.reporterFor(storyPath, format);
		}
	}

}
