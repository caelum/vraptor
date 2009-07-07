package br.com.caelum.vraptor.vraptor2;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.DefaultConverters;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;

/**
 * An adaptor between vraptor2 converter list and vraptor 3.<br>
 * If you use the default converters delegate, it requires the register
 * container in order to register new converters.
 * 
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class VRaptor2Converters implements Converters {

	private final Converters vraptor3;
	private final List<org.vraptor.converter.Converter> converterList = new ArrayList<org.vraptor.converter.Converter>();

	@SuppressWarnings("unchecked")
	public VRaptor2Converters(Config config, Converters delegateConverters) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		this.vraptor3 = delegateConverters;
		List<String> list = config.getConverters();
		for (String l : list) {
			Class<? extends org.vraptor.converter.Converter> converterType = (Class<? extends org.vraptor.converter.Converter>) Class
					.forName(l);
			converterList.add(converterType.newInstance());
		}
	}

	public Converter<?> to(Class<?> type, Container container) {
		for (org.vraptor.converter.Converter converter : converterList) {
			for (Class<?> supported : converter.getSupportedTypes()) {
				if (supported.isAssignableFrom(type)) {
					return new ConverterWrapper(converter);
				}
			}
		}
		return vraptor3.to(type, container);
	}

	public void init() {
		vraptor3.init();
	}

	public void register(Class<? extends Converter<?>> converterClass) {
		throw new UnsupportedOperationException(
				"cannot add vr3 converters in vr2 converters container (or should we delegate?");
	}

}
