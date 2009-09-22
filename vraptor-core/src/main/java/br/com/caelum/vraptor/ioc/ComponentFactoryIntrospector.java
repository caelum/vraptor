package br.com.caelum.vraptor.ioc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author Fabio Kung
 */
public class ComponentFactoryIntrospector {

	public Class<?> targetTypeForComponentFactory(Class<?> type) {

		Class<?> c = targetTypeForComponentFactory0(type);
		if (c == null) {
			throw new ComponentRegistrationException("Class does not implements ComponentFactory " + type);
		}
		return c;
	}

	private Class<?> targetTypeForComponentFactory0(Class<?> type) {
		Type[] interfaces = type.getGenericInterfaces();
		for (Type implemented : interfaces) {
			if (implemented instanceof ParameterizedType) {
				Type rawType = ((ParameterizedType) implemented).getRawType();
				Type[] typeArguments = ((ParameterizedType) implemented).getActualTypeArguments();
				if (ComponentFactory.class.equals(rawType)) {
					return (Class) typeArguments[0];
				}
			} else {
				if (ComponentFactory.class.equals(implemented)) {
					// implementing ComponentFactory WITHOUT declaring the
					// parameterized type! (or bounded)
					throw new ComponentRegistrationException(
							"The class implementing ComponentFactory<T> must define the generic argument. Eg.: "
									+ "public class MyFactory implements ComponentFactory<MyComponent> { ... }");
				}
			}
		}

		// maybe the superclass implements the interface:
		{
			Class<?> superClass = type.getSuperclass();
			if (superClass != null) {
				Class<?> c = targetTypeForComponentFactory0(superClass);
				if (c != null)
					return c;
			}
		}

		// maybe a interface extends it:
		{
			for (Class<?> clazz : type.getInterfaces()) {
				Class<?> c = targetTypeForComponentFactory0(clazz);
				if (c != null)
					return c;
			}
		}

		return null;
	}

}
