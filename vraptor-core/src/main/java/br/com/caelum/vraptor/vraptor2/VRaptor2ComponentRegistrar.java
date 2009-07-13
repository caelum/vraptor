package br.com.caelum.vraptor.vraptor2;

import java.util.Collection;

import org.vraptor.annotations.Component;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.pico.Registrar;
import br.com.caelum.vraptor.ioc.pico.Scanner;

public class VRaptor2ComponentRegistrar implements Registrar {
	private final ComponentRegistry components;

	public VRaptor2ComponentRegistrar(ComponentRegistry components) {
		this.components = components;
	}

	@Override
	public void registerFrom(Scanner scanner) {
		Collection<Class<?>> componentTypes = scanner.getTypesWithAnnotation(Component.class);
		for (Class<?> type : componentTypes) {
			components.register(type, type);
		}
	}
}
