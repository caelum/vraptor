package br.com.caelum.vraptor.ioc.spring.components;

import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

@Convert(Foo.class)
@ApplicationScoped
public class DummyConverter implements Converter<Foo> {
	public Foo convert(String value, Class<? extends Foo> type, ResourceBundle bundle) {
		return new Foo();
	}
}
