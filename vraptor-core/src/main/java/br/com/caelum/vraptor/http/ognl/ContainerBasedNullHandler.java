package br.com.caelum.vraptor.http.ognl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import ognl.NullHandler;

public class ContainerBasedNullHandler implements NullHandler {

    public Object nullMethodResult(Map context, Object target, String methodName, Object[] args) {
        return null;
    }

    public Object nullPropertyValue(Map context, Object target, Object property) {
        Method method = findMethod(target.getClass(), "get" + translate((String) property), target.getClass());
        Type returnType = method.getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) returnType;
            returnType = paramType.getRawType();
        }
        try {
            Object instance = ((Class<?>) returnType).newInstance();
            Method setter= findMethod(target.getClass(), "set" + translate((String) property), target.getClass(), method.getReturnType());
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

    private Method findMethod(Class<? extends Object> type, String name, Class<? extends Object> baseType, Class<?> ...params) {
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
