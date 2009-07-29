package br.com.caelum.vraptor.core;

import java.lang.annotation.Annotation;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

@ApplicationScoped
public class ComponentHandler implements StereotypeHandler {
	public void handle(Class<?> Type) {
		//NOP
	}
	public Class<? extends Annotation> stereotype() {
		return Component.class;
	}

}
