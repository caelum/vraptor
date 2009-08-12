package br.com.caelum.vraptor.ioc.fixture;

import javax.annotation.PreDestroy;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;

@Component
@ApplicationScoped
public class ComponentFactoryInTheClasspath implements ComponentFactory<br.com.caelum.vraptor.ioc.fixture.ComponentFactoryInTheClasspath.Provided> {
	public int callsToPreDestroy = 0 ;
	
	@PreDestroy
	public void preDestroy() {
		callsToPreDestroy++;
	}
	
	
	public static class Provided {
		private Provided() {}
	}
	public static Provided PROVIDED = new Provided();

	public Provided getInstance() {
		return PROVIDED;
	}

}
