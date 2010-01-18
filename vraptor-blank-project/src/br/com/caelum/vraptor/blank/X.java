package br.com.caelum.vraptor.blank;

import javax.servlet.ServletContext;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;

@RequestScoped
@Component
public class X {

	public X(ServletContext context) {
System.out.println(context);
	}
}
