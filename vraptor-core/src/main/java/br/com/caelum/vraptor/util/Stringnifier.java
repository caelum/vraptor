package br.com.caelum.vraptor.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class Stringnifier {

	static String argumentsToString(Class<?>[] parameterTypes) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < parameterTypes.length; i++) {
			builder.append(parameterTypes[i].getSimpleName());
			if (i != parameterTypes.length - 1)
				builder.append(", ");
		}
		return "(" + builder.toString() + ")";
	}

	public static String simpleNameFor(AccessibleObject object) {
		if (object instanceof Method) {

			Method method = (Method) object;
			return method.getDeclaringClass().getSimpleName() + "." + method.getName()
					+ argumentsToString(method.getParameterTypes());
		}
		if (object instanceof Constructor<?>) {
			Constructor<?> constructor = (Constructor<?>) object;
			return constructor.getDeclaringClass().getSimpleName() + argumentsToString(constructor.getParameterTypes());
		}
		throw new IllegalArgumentException("can only strignify constructors and methods");

	}
}
