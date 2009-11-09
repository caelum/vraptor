package br.com.caelum.vraptor.rest;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper to create transitions and states when using restfulie.
 * 
 * @author guilherme silveira
 * @since 3.0.3
 *
 */
public class DefaultStateControl implements StateControl{
	
	private final List<TransitionBuilder> transitions = new ArrayList<TransitionBuilder>();
	private final List<StateBuilder> states = new ArrayList<StateBuilder>();
	
	public String getStatusField() {
		return "status";
	}
	
	public TransitionBuilder transition(String name) {
		TransitionBuilder builder = new TransitionBuilder(this, name);
		this.transitions.add(builder);
		return builder;
	}

	public StateBuilder state(String name) {
		return new StateBuilder(this,name);
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
