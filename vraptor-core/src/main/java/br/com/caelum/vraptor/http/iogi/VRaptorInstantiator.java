package br.com.caelum.vraptor.http.iogi;

import java.lang.reflect.AccessibleObject;
import java.util.Arrays;
import java.util.List;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.MultiInstantiator;
import br.com.caelum.iogi.ObjectInstantiator;
import br.com.caelum.iogi.collections.ArrayInstantiator;
import br.com.caelum.iogi.collections.ListInstantiator;
import br.com.caelum.iogi.conversion.StringConverter;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;
import br.com.caelum.iogi.spi.ParameterNamesProvider;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.ioc.Container;

import com.google.common.collect.ImmutableList;

public class VRaptorInstantiator implements Instantiator<Object> {
	private final Converters converters;
	private final Container container;
	private final Localization localization;
	private MultiInstantiator multiInstantiator;
	private final ParameterNameProvider parameterNameProvider;
	
	public VRaptorInstantiator(Converters converters, Container container, Localization localization, ParameterNameProvider parameterNameProvider) {
		this.converters = converters;
		this.container = container;
		this.localization = localization;
		this.parameterNameProvider = parameterNameProvider;
		
		DependencyProvider dependencyProvider = new VRaptorDependencyProvider();
		VRaptorParameterNamesProvider parameterNamesProvider = new VRaptorParameterNamesProvider();
		
		List<Instantiator<?>> instantiatorList = ImmutableList.of( 
			new VRaptorTypeConverter(),
			new StringConverter(),
			new ArrayInstantiator(this),
			new NullDecorator(new ListInstantiator(this)), //NOTE: NullDecorator is here to preserve existing behaviour. Don't know if it is the ideal one, though.
			new NullDecorator(new ObjectInstantiator(this, dependencyProvider, parameterNamesProvider)));
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
	
	private final class VRaptorTypeConverter implements Instantiator<Object> {
		@Override
		public boolean isAbleToInstantiate(Target<?> target) {
			return converters.existsFor(target.getClassType(), container);
		}

		@Override
		public Object instantiate(Target<?> target, Parameters parameters) {
			Parameter parameter = parameters.namedAfter(target);
			final String stringValue = parameter != null ? parameter.getValue() : null;
			return converterForTarget(target).convert(stringValue, target.getClassType(), localization.getBundle());
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
	
	private final class VRaptorParameterNamesProvider implements ParameterNamesProvider {

		@Override
		public List<String> lookupParameterNames(AccessibleObject methodOrConstructor) {
			return Arrays.asList(parameterNameProvider.parameterNamesFor(methodOrConstructor));
		}
		
	}
	
	private final class NullDecorator implements Instantiator<Object> {
		private final Instantiator<?> delegateInstantiator;

		public NullDecorator(Instantiator<?> delegateInstantiator) {
			this.delegateInstantiator = delegateInstantiator;
		}

		@Override
		public boolean isAbleToInstantiate(Target<?> target) {
			return delegateInstantiator.isAbleToInstantiate(target);
		}
		
		@Override
		public Object instantiate(Target<?> target, Parameters parameters) {
			if (!parameters.hasRelatedTo(target)) 
				return null;
			return delegateInstantiator.instantiate(target, parameters);
		}
	}
}
