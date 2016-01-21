/**
 *
 */
package br.com.caelum.vraptor.ioc.fixture;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.NeedsCustomInstantiation;

@Component
public class DependentOnSomethingFromComponentFactory {
	private final NeedsCustomInstantiation dependency;

	public DependentOnSomethingFromComponentFactory(NeedsCustomInstantiation dependency) {
		this.dependency = dependency;
	}

	public NeedsCustomInstantiation getDependency() {
		return dependency;
	}
}