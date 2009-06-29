package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.core.Converters;

public class ConverterAcceptor implements Acceptor {

	private final Converters converters;

	public ConverterAcceptor(Converters converters) {
		this.converters = converters;
	}

	public void analyze(Class<?> type) {
		if (type.isAnnotationPresent(Convert.class)) {

		}
	}
}
