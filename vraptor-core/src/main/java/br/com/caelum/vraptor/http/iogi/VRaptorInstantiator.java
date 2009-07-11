package br.com.caelum.vraptor.http.iogi;

import java.util.List;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.MultiInstantiator;
import br.com.caelum.iogi.ObjectInstantiator;
import br.com.caelum.iogi.collections.ArrayInstantiator;
import br.com.caelum.iogi.collections.ListInstantiator;
import br.com.caelum.iogi.conversion.StringConverter;
import br.com.caelum.iogi.conversion.TypeConverter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.ioc.Container;

import com.google.common.collect.ImmutableList;

public class VRaptorInstantiator implements Instantiator<Object> {
	private final Converters converters;
	private final Container container;
	private final Localization localization;
	private MultiInstantiator multiInstantiator;
	
	public VRaptorInstantiator(Converters converters, Container container, Localization localization) {
		this.converters = converters;
		this.container = container;
		this.localization = localization;
		DependencyProvider dependencyProvider = new VRaptorDependencyProvider();
		List<Instantiator<?>> instantiatorList = new ImmutableList.Builder<Instantiator<?>>()
			.add(new VRaptorConvertersInstantiator())
			.add(new StringConverter())
			.add(new ArrayInstantiator(this))
			.add(new ListInstantiator(this))
			.add(new ObjectInstantiator(this, dependencyProvider))
			.build();
		multiInstantiator = new MultiInstantiator(instantiatorList);
	}
	
	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return true;
	}

	@Override
	public Object instantiate(Target<?> target, Parameters parameters) {
		return multiInstantiator.instantiate(target, parameters);
	}
	
	private final class VRaptorConvertersInstantiator extends TypeConverter<Object> {
		@Override
		public boolean isAbleToInstantiate(Target<?> target) {
			return converters.existsFor(target.getClassType(), container);
		}

		@Override
		protected Object convert(String stringValue, Target<?> to) throws Exception {
			return converterForTarget(to).convert(stringValue, to.getClassType(), localization.getBundle());
		}

		@SuppressWarnings("unchecked")
		private Converter<Object> converterForTarget(Target<?> target) {
			return (Converter<Object>) converters.to(target.getClassType(), container);
		}		
	}
	
	private final class VRaptorDependencyProvider implements DependencyProvider {
		@Override
		public boolean canProvide(Target<?> target) {
			return container.instanceFor(target.getClassType()) != null;
		}

		@Override
		public Object provide(Target<?> target) {
			return container.instanceFor(target.getClassType());
		}
	}
}
