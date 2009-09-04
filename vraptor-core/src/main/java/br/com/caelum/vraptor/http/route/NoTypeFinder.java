package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

public class NoTypeFinder implements TypeFinder {

	public Map<String, Class<?>> getParameterTypes(Method method, String[] parameterPaths) {
		return Collections.emptyMap();
	}

}
