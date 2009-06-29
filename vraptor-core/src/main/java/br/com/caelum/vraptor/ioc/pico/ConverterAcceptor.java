package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.core.Converters;

public class ConverterAcceptor implements Acceptor {

	private final Converters converters;

	public ConverterAcceptor(Converters converters) {
		this.converters = converters;
	}

	@SuppressWarnings("unchecked")
	public void analyze(Class<?> type) {
		if (type.isAnnotationPresent(Convert.class)) {
			if (!Converter.class.isAssignableFrom(type)) {
				throw new VRaptorException("converter does not implement Converter");
			}
			converters.register(Class.class.cast(type));
		}
	}
}
