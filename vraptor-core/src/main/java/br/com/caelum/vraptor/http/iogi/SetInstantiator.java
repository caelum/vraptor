package br.com.caelum.vraptor.http.iogi;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.collections.ListInstantiator;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;

/**
 * An instantiator that supports Sets.
 * 
 * @author Otávio Scherer Garcia
 * @author Lucas Cavalcanti
 * @since 3.5.0-SNAPSHOT
 */
public class SetInstantiator implements Instantiator<Set<Object>> {
	
	private ListInstantiator listInstantiator;

	public SetInstantiator(Instantiator<Object> listElementInstantiator) {
		listInstantiator = new ListInstantiator(listElementInstantiator);
		
	}
	
	public boolean isAbleToInstantiate(Target<?> target) {
		return target.getClassType().isAssignableFrom(Set.class);
	}
	
	public Set<Object> instantiate(Target<?> target, Parameters parameters) {
		List<Object> list = listInstantiator.instantiate(target, parameters);
		
		if (list == null) {
			return null;
		}
		
		return new HashSet<Object>(list);
	}
}
