package br.com.caelum.vraptor.rest;

import java.util.List;

/**
 * Basic restfulie implementation: use this interface to implement and let the
 * serializer know which are the possible followign transitions from this
 * object's state.
 * 
 * @author guilherme silveira
 * @author caires vinicius
 * @since 3.0.3
 * 
 */
public interface StateResource {
	public List<Transition> getFollowingTransitions();
}
