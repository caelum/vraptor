/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.http.iogi;

import java.lang.reflect.AccessibleObject;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.MultiInstantiator;
import br.com.caelum.iogi.ObjectInstantiator;
import br.com.caelum.iogi.collections.ArrayInstantiator;
import br.com.caelum.iogi.collections.ListInstantiator;
import br.com.caelum.iogi.conversion.FallbackConverter;
import br.com.caelum.iogi.conversion.StringConverter;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;
import br.com.caelum.iogi.spi.ParameterNamesProvider;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.converter.ConversionError;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationMessage;
import br.com.caelum.vraptor.validator.annotation.ValidationException;

import com.google.common.collect.ImmutableList;

@Component
@RequestScoped
public class VRaptorInstantiator implements InstantiatorWithErrors, Instantiator<Object> {
	private final Converters converters;
	private final Container container;
	private final Localization localization;
	private final MultiInstantiator multiInstantiator;
	private final ParameterNameProvider parameterNameProvider;
	private final HttpServletRequest request;
	private List<Message> errors;

	public VRaptorInstantiator(Converters converters, Container container, Localization localization, ParameterNameProvider parameterNameProvider, HttpServletRequest request) {
		this.converters = converters;
		this.container = container;
		this.localization = localization;
		this.parameterNameProvider = parameterNameProvider;
		this.request = request;

		DependencyProvider dependencyProvider = new VRaptorDependencyProvider();
		ParameterNamesProvider parameterNamesProvider = new VRaptorParameterNamesProvider();

		ObjectInstantiator objectInstantiator = new ObjectInstantiator(this, dependencyProvider, parameterNamesProvider);
		List<Instantiator<?>> instantiatorList = ImmutableList.of(
			new RequestAttributeInstantiator(),
			new VRaptorTypeConverter(),
			FallbackConverter.fallbackToNull(new StringConverter()),
			new ArrayInstantiator(this),
			new NullDecorator(new ListInstantiator(this)), //NOTE: NullDecorator is here to preserve existing behaviour. Don't know if it is the ideal one, though.
			new DependencyInstantiator(objectInstantiator),
			objectInstantiator);
		multiInstantiator = new MultiInstantiator(instantiatorList);
	}

	public boolean isAbleToInstantiate(Target<?> target) {
		return true;
	}

	public Object instantiate(Target<?> target, Parameters parameters, List<Message> errors) {
		this.errors = errors;
		return instantiate(target, parameters);
	}

	public Object instantiate(Target<?> target, Parameters parameters) {
		try {
			return multiInstantiator.instantiate(target, parameters);
		} catch(Exception e) {
			handleException(target, e);
			return null;
		}
	}
	private void handleException(Target<?> target, Throwable e) {
		if (e.getClass().isAnnotationPresent(ValidationException.class)) {
			errors.add(new ValidationMessage(e.getLocalizedMessage(), target.getName()));
		} else if (e.getCause() == null) {
			throw new InvalidParameterException("Exception when trying to instantiate " + target, e);
		} else {
			handleException(target, e.getCause());
		}
	}

	private final class RequestAttributeInstantiator implements Instantiator<Object> {
		public Object instantiate(Target<?> target, Parameters params) {
			return request.getAttribute(target.getName());
		}

		public boolean isAbleToInstantiate(Target<?> target) {
			return request.getAttribute(target.getName()) != null;
		}

	}

	private final class DependencyInstantiator implements Instantiator<Object> {
		private final Instantiator<Object> delegate;

		public DependencyInstantiator(Instantiator<Object> delegate) {
			this.delegate = delegate;
		}
		public Object instantiate(Target<?> target, Parameters params) {
			if (params.hasRelatedTo(target)) {
				return delegate.instantiate(target, params);
			}
			return container.instanceFor(target.getClassType());
		}

		public boolean isAbleToInstantiate(Target<?> target) {
			return container.canProvide(target.getClassType());
		}

	}

	private final class VRaptorTypeConverter implements Instantiator<Object> {
		public boolean isAbleToInstantiate(Target<?> target) {
			return !String.class.equals(target.getClassType()) && converters.existsFor(target.getClassType());
		}

		public Object instantiate(Target<?> target, Parameters parameters) {
			try {
				Parameter parameter = parameters.namedAfter(target);
				return converterForTarget(target).convert(parameter.getValue(), target.getClassType(), localization.getBundle());
			} catch (ConversionError ex) {
				errors.add(new ValidationMessage(ex.getMessage(), target.getName()));
			}
			return null;
		}

		@SuppressWarnings("unchecked")
		private Converter<Object> converterForTarget(Target<?> target) {
			return (Converter<Object>) converters.to(target.getClassType());
		}
	}

	private final class VRaptorDependencyProvider implements DependencyProvider {
		public boolean canProvide(Target<?> target) {
			return container.canProvide(target.getClassType());
		}

		public Object provide(Target<?> target) {
			return container.instanceFor(target.getClassType());
		}
	}

	private final class VRaptorParameterNamesProvider implements ParameterNamesProvider {
		public List<String> lookupParameterNames(AccessibleObject methodOrConstructor) {
			return Arrays.asList(parameterNameProvider.parameterNamesFor(methodOrConstructor));
		}
	}

	private final class NullDecorator implements Instantiator<Object> {
		private final Instantiator<?> delegateInstantiator;

		public NullDecorator(Instantiator<?> delegateInstantiator) {
			this.delegateInstantiator = delegateInstantiator;
		}

		public boolean isAbleToInstantiate(Target<?> target) {
			return delegateInstantiator.isAbleToInstantiate(target);
		}

		public Object instantiate(Target<?> target, Parameters parameters) {
			if (!parameters.hasRelatedTo(target)) {
				return null;
			}
			return delegateInstantiator.instantiate(target, parameters);
		}
	}
}
