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

    public Object nullPropertyValue(Map context, Object target, Object property) {

        OgnlContext ctx = (OgnlContext) context;
        int indexInParent = ctx.getCurrentEvaluation().getNode().getIndexInParent();
        int maxIndex = ctx.getRootEvaluation().getNode().jjtGetNumChildren() - 1;

        if (!(indexInParent != -1 && indexInParent < maxIndex)) {
            return null;
        }

        Method method = findMethod(target.getClass(), "get" + translate((String) property), target.getClass());
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) returnType;
            returnType = paramType.getRawType();
        }
        try {
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
            Object instance = typeToInstantiate.newInstance();
            Method setter = findMethod(target.getClass(), "set" + translate((String) property), target.getClass(),
                    method.getReturnType());
            setter.invoke(target, instance);
            return instance;
        } catch (InstantiationException e) {
            // TODO better
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            // TODO better
            throw new IllegalArgumentException(e);
        } catch (IllegalArgumentException e) {
            // TODO better
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            // TODO better
            throw new IllegalArgumentException(e);
        }
    }

    private String translate(String property) {
        return property.length() == 1 ? property.toUpperCase() : Character.toUpperCase(property.charAt(0))
                + property.substring(1);
    }

    private Method findMethod(Class<? extends Object> type, String name, Class<? extends Object> baseType,
            Class<?>... params) {
        try {
            return type.getDeclaredMethod(name, params);
        } catch (SecurityException e) {
            // TODO better
            throw new IllegalArgumentException(e);
        } catch (NoSuchMethodException e) {
            if (type.equals(Object.class)) {
                // TODO better
                throw new IllegalArgumentException("Unable to find getter for " + name + " @ " + baseType.getName());
            }
            return findMethod(type.getSuperclass(), name, type, params);
        }
    }

}
