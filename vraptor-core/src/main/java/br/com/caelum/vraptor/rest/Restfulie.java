package br.com.caelum.vraptor.rest;

import java.util.List;

/**
 * @author lucas cavalcanti
 * @author guilherme silveira
 * @since 3.0.3
 */
public interface Restfulie {

	public TransitionBuilder transition(String name);
	<T> T transition(Class<T> type);

	public StateBuilder state(String name);

	public List<Transition> getTransitions();

	public List<State> getStates();
	
	/**
	 * TODO Should be removed soon.
	 */
	public void clear();

}
