/**
 * 
 */
package br.com.caelum.vraptor.http.iogi;

import java.util.List;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.collections.ArrayInstantiator;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

final class ArrayAdapter implements Instantiator<Object> {

	private final ArrayInstantiator delegate;

	public ArrayAdapter(ArrayInstantiator arrayInstantiator) {
		this.delegate = arrayInstantiator;
	}

	public Object instantiate(final Target<?> target, Parameters parameters) {
		List<Parameter> fixed = Lists.transform(parameters.forTarget(target), new Function<Parameter, Parameter>() {
			int i = 0;
			public Parameter apply(Parameter parameter) {
				if (target.getName().equals(parameter.getName())) {
					return new Parameter(parameter.getName() + "[" + i++ + "]", parameter.getValue());
				}
				return parameter;
			}
		});
		return delegate.instantiate(target, new Parameters(fixed));
	}

	public boolean isAbleToInstantiate(Target<?> target) {
		return delegate.isAbleToInstantiate(target);
	}

}