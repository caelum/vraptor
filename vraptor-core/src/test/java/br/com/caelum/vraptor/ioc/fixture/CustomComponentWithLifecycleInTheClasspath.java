package br.com.caelum.vraptor.ioc.fixture;

import javax.annotation.PreDestroy;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

@Component
@ApplicationScoped
public class CustomComponentWithLifecycleInTheClasspath {
	public int callsToPreDestroy = 0 ;
	
	@PreDestroy
	public void preDestroy() {
		callsToPreDestroy++;
	}

}
