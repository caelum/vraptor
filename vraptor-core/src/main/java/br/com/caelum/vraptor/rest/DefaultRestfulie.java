package br.com.caelum.vraptor.rest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.core.Routes;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;

/**
 * Helper to create transitions and states when using restfulie.
 * 
 * @author guilherme silveira
 * @since 3.0.3
 */
@Component
@RequestScoped
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

	public List<State> getStates() {
		List<State> states = new ArrayList<State>();
		for(StateBuilder builder : this.states) {
			states.add(builder.build());
		}
		return states;
	}

	public <T> T transition(final Class<T> type) {
		return proxifier.proxify(type, new MethodInvocation<T>() {
			public Object intercept(T proxy, Method method, Object[] args,
					SuperMethod superMethod) {
				T instance = transition(method.getName()).uses(type);
				try {
					method.invoke(instance, args);
				} catch (Exception e) {
					throw new IllegalArgumentException("Unable to create transition for " + method.getName() + " within " + type.getName(), e);
				}
				return null;
			}
		});
	}

	public void clear() {
		transitions.clear();
	}

}
