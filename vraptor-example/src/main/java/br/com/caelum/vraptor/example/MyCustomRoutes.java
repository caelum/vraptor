package br.com.caelum.vraptor.example;

import br.com.caelum.vraptor.http.Router;
import br.com.caelum.vraptor.http.RoutesConfiguration;
import br.com.caelum.vraptor.http.Rules;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
public class MyCustomRoutes implements RoutesConfiguration{

	public void config(Router router) {
		router.add(new Rules() {{
			routeFor("/").is(ClientsController.class).list();
		}});
	}

}
