/**
 * 
 */
package br.com.caelum.vraptor.http.iogi;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;

final class NullDecorator implements Instantiator<Object> {
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