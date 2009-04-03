package br.com.caelum.vraptor.http.ognl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ognl.ObjectNullHandler;
import ognl.OgnlContext;

public class ReflectionBasedNullHandler extends ObjectNullHandler {

    private static final Map<Class<?>, Class<?>> CONCRETE_TYPES = new HashMap<Class<?>, Class<?>>();

    static {
        CONCRETE_TYPES.put(List.class, ArrayList.class);
    }

    @SuppressWarnings("unchecked")
    public Object nullPropertyValue(Map context, Object target, Object property) {

        OgnlContext ctx = (OgnlContext) context;

        int indexInParent = ctx.getCurrentEvaluation().getNode().getIndexInParent();
        int maxIndex = ctx.getRootEvaluation().getNode().jjtGetNumChildren() - 1;

        // TODO all those ifs should be methods in mapped types

        if (!(indexInParent != -1 && indexInParent < maxIndex)) {
            return null;
        }

        try {
            if (target instanceof List) {
                int position = (Integer) property;
                Object listHolder = ctx.getCurrentEvaluation().getPrevious().getSource();
                String listPropertyName = ctx.getCurrentEvaluation().getPrevious().getNode().toString();
                Method listSetter = findMethod(listHolder.getClass(), "set" + translate((String) listPropertyName),
                        target.getClass());
                Type[] types = listSetter.getGenericParameterTypes();
                Type type = types[0];
                if (!(type instanceof ParameterizedType)) {
                    // TODO better
                    throw new IllegalArgumentException("Vraptor does not support non-generic collection at "
                            + listSetter.getName());
                }
                Object instance = ((Class) ((ParameterizedType) type).getActualTypeArguments()[0]).getConstructor()
                        .newInstance();
                List list = (List) target;
                while (list.size() <= position) {
                    list.add(null);
                }
                list.set(position, instance);
                return instance;
            }

            Method method = findMethod(target.getClass(), "get" + translate((String) property), target.getClass());
            Type returnType = method.getGenericReturnType();
            if (returnType instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) returnType;
                returnType = paramType.getRawType();
            }
            Class<?> baseType = (Class<?>) returnType;
            if (baseType.isArray()) {
                // TODO better
                throw new IllegalArgumentException("Vraptor does not support array types: use lists instead!");
            }
            Class<?> typeToInstantiate = baseType;
            if (baseType.isInterface()) {
                if (!CONCRETE_TYPES.containsKey(baseType)) {
                    // TODO better
                    throw new IllegalArgumentException("Vraptor does not support this interface: "
                            + typeToInstantiate.getName());
                }
                typeToInstantiate = CONCRETE_TYPES.get(baseType);
            }
            Object instance = typeToInstantiate.getConstructor().newInstance();
            Method setter = findMethod(target.getClass(), "set" + translate((String) property), target.getClass());
            setter.invoke(target, instance);
            return instance;
        } catch (InstantiationException e) {
            // TODO better
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            // TODO better
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            // TODO better
            throw new IllegalArgumentException(e);
        } catch (SecurityException e) {
            // TODO better
            throw new IllegalArgumentException(e);
        } catch (NoSuchMethodException e) {
            // TODO better
            throw new IllegalArgumentException(e);
        }
    }

    private String translate(String property) {
        return property.length() == 1 ? property.toUpperCase() : Character.toUpperCase(property.charAt(0))
                + property.substring(1);
    }

    private Method findMethod(Class<? extends Object> type, String name, Class<? extends Object> baseType) {
        Method[] methods = type.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(name)) {
                return method;
            }
        }
        if (type.equals(Object.class)) {
            // TODO better
            throw new IllegalArgumentException("Unable to find method for " + name + " @ " + baseType.getName());
        }
        return findMethod(type.getSuperclass(), name, type);
    }

}
