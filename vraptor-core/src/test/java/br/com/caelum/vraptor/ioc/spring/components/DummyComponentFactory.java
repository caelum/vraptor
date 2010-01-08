package br.com.caelum.vraptor.ioc.spring.components;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;

@Component
public class DummyComponentFactory implements ComponentFactory<Foo>{

	public Foo getInstance() {
		return null;
	}

}
