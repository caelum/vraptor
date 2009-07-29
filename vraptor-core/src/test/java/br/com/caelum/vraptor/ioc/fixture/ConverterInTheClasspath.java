package br.com.caelum.vraptor.ioc.fixture;

import java.util.ResourceBundle;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;

@Convert(Void.class)
public class ConverterInTheClasspath implements Converter<Void> {
	public Void convert(String value, Class<? extends Void> type, ResourceBundle bundle) {
		return null;
	}
}
