package br.com.caelum.vraptor.http;

import java.lang.reflect.Method;

/**
 * Provides all parameter names for an specific java method.
 * 
 * @author Guilherme Silveira
 */
public interface ParameterNameProvider {

    String[] parameterNamesFor(Method method);

}
