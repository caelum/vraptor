
package br.com.caelum.vraptor.http.ognl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import ognl.Evaluation;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.vraptor2.Info;

/**
 * Capable of instantiating lists. These are registered for later removal of
 * null entitres.
 * 
 * @author Guilherme Silveira
 */
class ListNullHandler {

	@SuppressWarnings("unchecked")
	Object instantiate(Container container, Object target, Object property, Evaluation evaluation)
			throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		// creating instance
		Object listHolder = evaluation.getSource();
		String listPropertyName = evaluation.getNode().toString();
		Method listSetter = ReflectionBasedNullHandler.findMethod(listHolder.getClass(), "set"
				+ Info.capitalize(listPropertyName), target.getClass(), null);
		Type[] types = listSetter.getGenericParameterTypes();
		Type type = types[0];
		if (!(type instanceof ParameterizedType)) {
			throw new VRaptorException("Vraptor does not support non-generic collection at "
					+ listSetter.getName());
		}
		Class typeToInstantiate = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
		Constructor constructor = typeToInstantiate.getDeclaredConstructor();
		constructor.setAccessible(true);
		Object instance = constructor.newInstance();

		// setting the position
		int position = (Integer) property;
		List list = (List) target;
		while (list.size() <= position) {
			list.add(null);
		}
		list.set(position, instance);

		// registering for null entries removal
		EmptyElementsRemoval removal = container.instanceFor(EmptyElementsRemoval.class);
		removal.add(list);

		return instance;
	}

}
