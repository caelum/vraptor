package br.com.caelum.vraptor.rest;

import java.lang.reflect.Method;

/**
 * Represents a transition between a resource state and another.
 * 
 * @author guilherme silveira
 * @author pedro mariano
 * @since 3.0.3
 *
 */
public interface Transition {

	String getUri();

	String getName();

	/**
	 * Whether this transition uses this specific method in order to get executed.
	 */
	boolean matches(Method method);

}
