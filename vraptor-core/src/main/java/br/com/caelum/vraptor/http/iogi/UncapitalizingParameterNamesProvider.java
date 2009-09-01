/**
 * 
 */
package br.com.caelum.vraptor.http.iogi;

import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.vraptor2.Info;

final class UncapitalizingParameterNamesProvider implements ParameterNameProvider {
	private final ParameterNameProvider delegate;
	
	public UncapitalizingParameterNamesProvider(ParameterNameProvider provider) {
		this.delegate = provider;
	}

	@Override
	public String[] parameterNamesFor(AccessibleObject methodOrConstructor) {
		String[] originalNames = delegate.parameterNamesFor(methodOrConstructor);
		
		List<String> uncapitalizedNames = new ArrayList<String>();
		for (String parameterName : originalNames) {
			uncapitalizedNames.add(Info.decapitalize(parameterName));
		}
		
		return (String[]) uncapitalizedNames.toArray(new String[uncapitalizedNames.size()]);
	}

}