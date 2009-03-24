package br.com.caelum.vraptor.ioc.pico;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import br.com.caelum.vraptor.core.Request;
import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.http.StupidTranslator;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.DefaultDirScanner;
import br.com.caelum.vraptor.resource.DefaultResourceRegistry;
import br.com.caelum.vraptor.resource.ResourceMethod;
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
		this.container.addComponent(this);
		this.container.addComponent(context);
		this.container.addComponent(StupidTranslator.class);
		this.container.addComponent(DefaultResourceRegistry.class);
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

	public Request prepareFor(ResourceMethod method, HttpServletRequest request, HttpServletResponse response) {
		PicoBasedRequestContainer container = new PicoBasedRequestContainer(this.container, method, request,response);
		return container.withA(RequestExecution.class);
	}

}
