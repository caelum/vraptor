package br.com.caelum.vraptor.vraptor2;

import java.util.Collection;

import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.pico.Registrar;
import br.com.caelum.vraptor.ioc.pico.Scanner;
import br.com.caelum.vraptor.resource.DefaultResourceClass;

@ApplicationScoped
public class VRaptor2ComponentRegistrar implements Registrar {
	private final ComponentRegistry components;
	private final Router router;

	public VRaptor2ComponentRegistrar(ComponentRegistry components, Router router) {
		this.components = components;
		this.router = router;
	}

	public void registerFrom(Scanner scanner) {
		Collection<Class<?>> componentTypes = scanner.getTypesWithAnnotation(Component.class);
		for (Class<?> type : componentTypes) {
			components.register(type, type);
			router.register(new DefaultResourceClass(type));
		}
	}
}
