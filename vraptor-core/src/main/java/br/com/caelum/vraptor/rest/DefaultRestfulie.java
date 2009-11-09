package br.com.caelum.vraptor.rest;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.core.Routes;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.proxy.Proxifier;

/**
 * Helper to create transitions and states when using restfulie.
 * 
 * @author guilherme silveira
 * @since 3.0.3
 *
 */
@ApplicationScoped
@Component
public class DefaultRestfulie implements Restfulie {
	
	private final List<TransitionBuilder> transitions = new ArrayList<TransitionBuilder>();
	private final List<StateBuilder> states = new ArrayList<StateBuilder>();
	private final Routes routes;
	private final Proxifier proxifier;
	
	public DefaultRestfulie(Routes routes, Proxifier proxifier) {
		this.routes = routes;
		this.proxifier = proxifier;
	}
	
	public String getStatusField() {
		return "status";
	}
	
	public TransitionBuilder transition(String name) {
		TransitionBuilder builder = new TransitionBuilder(name, routes, proxifier);
		this.transitions.add(builder);
		return builder;
	}

	public StateBuilder state(String name) {
		return new StateBuilder(name);
	}

	public List<Transition> getTransitions() {
		List<Transition> transitions = new ArrayList<Transition>();
		for(TransitionBuilder builder : this.transitions) {
			transitions.add(builder.build());
		}
		return transitions;
	}

	public String getStateField() {
		return null;
	}

	public List<State> getStates() {
		List<State> states = new ArrayList<State>();
		for(StateBuilder builder : this.states) {
			states.add(builder.build());
		}
		return states;
	}

}
