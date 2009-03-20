package br.com.caelum.vraptor.ioc;

import javax.servlet.ServletContext;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import br.com.caelum.vraptor.resource.DefaultDirScanner;
import br.com.caelum.vraptor.resource.WebInfClassesScanner;

/**
 * Managing internal components by using pico container.
 * 
 * @author Guilherme Silveira
 */
public class PicoBasedContainer implements Container {

	private final MutablePicoContainer container;

	public PicoBasedContainer(ServletContext context) {
		this.container = new PicoBuilder().withCaching().build();
		this.container.addComponent(context);
		this.container.addComponent(DefaultDirScanner.class);
		this.container.addComponent(WebInfClassesScanner.class);
	}

	public <T> T withA(Class<T> type) {
		return container.getComponent(type);
	}

	public void start() {
		container.start();
	}

	public void stop() {
		container.stop();
	}

}
