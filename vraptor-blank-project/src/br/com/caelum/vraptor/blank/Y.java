package br.com.caelum.vraptor.blank;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;


@RequestScoped
@Component
public class Y {

	public Y(X x) {

	}
}
