
package br.com.caelum.vraptor.resource;

import java.lang.reflect.Method;

/**
 * An identifier for a resource accesible web method.
 * 
 * @author guilherme silveira
 */
public interface ResourceMethod {

	Method getMethod();

	ResourceClass getResource();

}
