package br.com.caelum.vraptor.rest;

import java.util.List;

/**
 * Resources implementing this interface will be serialized with their links.
 * @author guilherme silveira
 * @author caires vinicius
 * @since 3.0.3
 *
 */
public interface StateControl {

	public String getStateField();

	public List<Transition> getTransitions(Restfulie control);

}
