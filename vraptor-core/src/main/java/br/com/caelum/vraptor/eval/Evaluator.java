
package br.com.caelum.vraptor.eval;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.vraptor2.Info;

/**
 * Evaluates expressions in order to access values.
 * 
 * @author guilherme silveira
 * 
 */
public class Evaluator {

	public Object get(Object root, String path) {
		String[] paths = path.split("[\\]\\.]");
		Object current = root;
		for (String p : paths) {
			try {
				current = navigate(current, p);
			} catch (InvocationTargetException e) {
				throw new VRaptorException("Unable to evaluate expression " + path, e.getCause());
			} catch (Exception e) {
				throw new VRaptorException("Unable to evaluate expression " + path, e);
			}
			if (current == null) {
				return "";
			}
		}
		return current;
	}

	private Object navigate(Object current, String path) throws IllegalArgumentException, SecurityException,
			IllegalAccessException, InvocationTargetException {
		int index = path.indexOf("[");
		int position = -1;
		if (index != -1) {
			position = Integer.parseInt(path.substring(index + 1));
			path = path.substring(0, index);
		}
		Method method;
		try {
			method = current.getClass().getDeclaredMethod("get" + Info.capitalize(path));
		} catch (NoSuchMethodException e) {
			try {
				method = current.getClass().getDeclaredMethod("is" + Info.capitalize(path));
			} catch (NoSuchMethodException e1) {
				throw new InvocationTargetException(e1, "Unable to find get or is method.");
			}
		}
		method.setAccessible(true);
		Object instance = method.invoke(current);
		if (index != -1) {
			instance = access(instance, position);
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	private Object access(Object current, int position) throws InvocationTargetException {
		if (current.getClass().isArray()) {
			return Array.get(current, position);
		} else if (List.class.isAssignableFrom(current.getClass())) {
			return ((List) current).get(position);
		} else if (Collection.class.isAssignableFrom(current.getClass())) {
			Iterator it = ((Collection) current).iterator();
			for (int i = 0; i < position; i++) {
				it.next();
			}
			return it.next();
		}
		String msg = "Unable to access position of a" + current.getClass().getName() + ".";
		throw new InvocationTargetException(new VRaptorException(msg), msg);
	}

	public static void main(String[] args) {
		System.out.println(Arrays.toString("client.favoriteColors[1]".split("[\\]\\.]")));
	}
}
