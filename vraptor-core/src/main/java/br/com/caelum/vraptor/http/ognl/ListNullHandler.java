package br.com.caelum.vraptor.http.ognl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import ognl.OgnlContext;
import br.com.caelum.vraptor.http.EmptyElementsRemoval;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.vraptor2.Info;

public class ListNullHandler {

    Object instantiate(Map context, Object target, Object property, OgnlContext ctx)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        int position = (Integer) property;
        Object listHolder = ctx.getCurrentEvaluation().getPrevious().getSource();
        String listPropertyName = ctx.getCurrentEvaluation().getPrevious().getNode().toString();
        Method listSetter = ReflectionBasedNullHandler.findMethod(listHolder.getClass(), "set"
                + Info.capitalize((String) listPropertyName), target.getClass());
        Type[] types = listSetter.getGenericParameterTypes();
        Type type = types[0];
        if (!(type instanceof ParameterizedType)) {
            // TODO better
            throw new IllegalArgumentException("Vraptor does not support non-generic collection at "
                    + listSetter.getName());
        }
        Class typeToInstantiate = (Class) ((ParameterizedType) type).getActualTypeArguments()[0];
        Object instance = typeToInstantiate.getConstructor().newInstance();
        List list = (List) target;
        while (list.size() <= position) {
            list.add(null);
        }
        Container container = (Container) context.get(Container.class);
        EmptyElementsRemoval removal = container.instanceFor(EmptyElementsRemoval.class);
        removal.add(list);
        list.set(position, instance);
        return instance;
    }

}
