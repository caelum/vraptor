package br.com.caelum.vraptor.http;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.Info;

/**
 * A class that provides default intended implementation for instanceWithParameters method.
 * TypeCreator implementations should extend this class.
 * @author Lucas Cavalcanti
 *
 */
public abstract class AbstractTypeCreator implements TypeCreator {

	private final ParameterNameProvider provider;

	public AbstractTypeCreator(ParameterNameProvider provider) {
		this.provider = provider;
	}

	public final Object instanceWithParameters(ResourceMethod method, Object... parameters) {
		String[] names = provider.parameterNamesFor(method.getMethod());
		Class<?> parameterType = typeFor(method);
		try {
			Object root = parameterType.getConstructor().newInstance();
			for (int i = 0; i < names.length; i++) {
				Method setter = findSetter(parameterType, "set" + Info.capitalize(names[i]));
				setter.invoke(root, parameters[i]);
			}
			return root;
		} catch (Exception e) {
			throw new VRaptorException(e);
		}
	}

	private Method findSetter(Class<?> parameterType, String methodName) {
		for (Method m : parameterType.getDeclaredMethods()) {
			if (m.getName().equals(methodName)) {
				return m;
			}
		}
		throw new VRaptorException(
				"Unable to instanciate parameters as setter method for parameter setting was not created. "
						+ "Thats probably a bug on your type creator. "
						+ "If you are using the default type creator, notify VRaptor.");
	}
}
