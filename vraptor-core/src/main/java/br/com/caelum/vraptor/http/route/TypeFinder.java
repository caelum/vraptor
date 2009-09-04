package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Discover Types from parameter paths
 * @author Lucas Cavalcanti
 *
 */
public interface TypeFinder {
	Map<String, Class<?>> getParameterTypes(Method method, String[] parameterPaths);
}
