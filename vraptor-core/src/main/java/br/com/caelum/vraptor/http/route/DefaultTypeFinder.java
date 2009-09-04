package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
/**
 * Discover parameter types
 * @author Lucas Cavalcanti
 *
 */
@ApplicationScoped
public class DefaultTypeFinder implements TypeFinder {

	private final ParameterNameProvider provider;
	public DefaultTypeFinder(ParameterNameProvider provider) {
		this.provider = provider;
	}
	public Map<String, Class<?>> getParameterTypes(Method method, String[] parameterPaths) {
		Map<String,Class<?>> result = new HashMap<String, Class<?>>();
		String[] parameterNamesFor = provider.parameterNamesFor(method);
		for (String path : parameterPaths) {
			for (int i = 0; i < parameterNamesFor.length; i++) {
				String name = parameterNamesFor[i];
				if (path.startsWith(name + ".") || path.equals(name)) {
					String[] items = path.split("\\.");
					Class<?> type = method.getParameterTypes()[i];
					for (int j = 1; j < items.length; j++) {
						String item = items[j];
						try {
							type = type.getDeclaredMethod("get" + upperFirst(item)).getReturnType();
						} catch (Exception e) {
							throw new IllegalArgumentException("Parameters paths are invalid: " + Arrays.toString(parameterPaths) + " for method " + method);
						}
					}
					result.put(path, type);
				}
			}
		}
		return result;
	}
	private String upperFirst(String item) {
		return item.substring(0, 1).toUpperCase() + item.substring(1);
	}

}
