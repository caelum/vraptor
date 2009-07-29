package br.com.caelum.vraptor.ioc.pico;

import java.lang.annotation.Annotation;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

@ApplicationScoped
public class ComponentHandler implements StereotypeHandler {
	
	public ComponentHandler() {
	}

	public void handle(Class<?> componentType) {

	}
	
	public Class<? extends Annotation> stereotype() {
		return Component.class;
	}
}
