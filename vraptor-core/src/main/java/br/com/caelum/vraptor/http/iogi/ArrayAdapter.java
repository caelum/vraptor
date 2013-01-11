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