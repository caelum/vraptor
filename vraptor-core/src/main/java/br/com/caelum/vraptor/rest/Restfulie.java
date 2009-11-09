package br.com.caelum.vraptor.rest;

import java.util.ArrayList;
import java.util.List;

public interface Restfulie {

	public TransitionBuilder transition(String name);

	public StateBuilder state(String name);

	public List<Transition> getTransitions();

	public List<State> getStates();

}
