/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.http.ognl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import br.com.caelum.vraptor.vraptor2.Info;

import ognl.ObjectNullHandler;
import ognl.OgnlContext;

public class ReflectionBasedNullHandler extends ObjectNullHandler {

    private static final Map<Class<?>, Class<?>> CONCRETE_TYPES = new HashMap<Class<?>, Class<?>>();

    static {
        CONCRETE_TYPES.put(List.class, ArrayList.class);
        CONCRETE_TYPES.put(Calendar.class, GregorianCalendar.class);
        CONCRETE_TYPES.put(Collection.class, ArrayList.class);
        CONCRETE_TYPES.put(Set.class, HashSet.class);
        CONCRETE_TYPES.put(SortedSet.class, TreeSet.class);
        CONCRETE_TYPES.put(Queue.class, LinkedList.class);
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
                Method listSetter = findMethod(listHolder.getClass(), "set" + Info.capitalize((String) listPropertyName),
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

            Method method = findMethod(target.getClass(), "get" + Info.capitalize((String) property), target.getClass());
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
            if (baseType.isInterface() || Modifier.isAbstract(baseType.getModifiers())) {
                if (!CONCRETE_TYPES.containsKey(baseType)) {
                    // TODO better
                    throw new IllegalArgumentException("Vraptor does not support this interface or abstract type: "
                            + typeToInstantiate.getName());
                }
                typeToInstantiate = CONCRETE_TYPES.get(baseType);
            }
            Object instance = typeToInstantiate.getConstructor().newInstance();
            Method setter = findMethod(target.getClass(), "set" + Info.capitalize((String) property), target.getClass());
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
