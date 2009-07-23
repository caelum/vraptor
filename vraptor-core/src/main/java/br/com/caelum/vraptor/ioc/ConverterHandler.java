package br.com.caelum.vraptor.ioc;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.core.Converters;

@ApplicationScoped
public class ConverterHandler implements StereotypeHandler {
	private Converters converters;

	public ConverterHandler(Converters converters) {
		this.converters = converters;
	}

	@Override
	public void handle(Class<?> annotatedType) {
		if (!(Converter.class.isAssignableFrom(annotatedType)))
			throw new VRaptorException("converter does not implement Converter");
		
		@SuppressWarnings("unchecked")
		Class<? extends Converter<?>> converterType = (Class<? extends Converter<?>>) annotatedType;
		
		converters.register(converterType);
	}

	@Override
	public boolean canHandle(Class<?> candidate) {
		return candidate.isAnnotationPresent(Convert.class);
	}
}
