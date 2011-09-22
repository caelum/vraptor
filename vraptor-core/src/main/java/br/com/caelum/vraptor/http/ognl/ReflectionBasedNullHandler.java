/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.http.ognl;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import net.vidageek.mirror.dsl.Mirror;
import ognl.ObjectNullHandler;
import ognl.OgnlContext;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.util.StringUtils;

/**
 * This null handler is a decorator for ognl api to invoke vraptor's api in
 * order to be able to instantiate collections, arrays and custom types whenever
 * the property is null.
 *
 * @author Guilherme Silveira
 */
public class ReflectionBasedNullHandler extends ObjectNullHandler {
    
    private final Proxifier proxifier;

    public ReflectionBasedNullHandler(Proxifier proxifier) {
        this.proxifier = proxifier;
	}

	@Override
    public Object nullPropertyValue(Map context, Object target, Object property) {

        OgnlContext ctx = (OgnlContext) context;

        EmptyElementsRemoval removal = (EmptyElementsRemoval) ctx.get("removal");

        NullHandler nullHandler = (NullHandler) ctx.get("nullHandler");
        ListNullHandler list = new ListNullHandler(removal);

        if (target == ctx.getRoot() && target instanceof List) {
        	return list.instantiate(target, property, (Type) context.get("rootType"));
        }

        int indexInParent = ctx.getCurrentEvaluation().getNode().getIndexInParent();
        int maxIndex = ctx.getRootEvaluation().getNode().jjtGetNumChildren() - 1;

        if (!(indexInParent != -1 && indexInParent < maxIndex)) {
        	return null;
        }

        if (target instanceof List) {
            return list.instantiate(target, property, list.getListType(target, ctx.getCurrentEvaluation().getPrevious(), ctx));
        }

        String propertyCapitalized = StringUtils.capitalize((String) property);
        Method getter = findGetter(target, propertyCapitalized);
        Type returnType = getter.getGenericReturnType();
        if (returnType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) returnType;
            returnType = paramType.getRawType();
        }

        Class<?> baseType = (Class<?>) returnType;
        Object instance;
        if (baseType.isArray()) {
            instance = instantiateArray(baseType);
        } else {
            instance = nullHandler.instantiate(baseType);
        }
        
        Method setter = findMethod(target.getClass(), "set" + propertyCapitalized, target.getClass(), getter.getReturnType());
        new Mirror().on(target).invoke().method(setter).withArgs(instance);
        return instance;
    }

    private Object instantiateArray(Class<?> baseType) {
        return Array.newInstance(baseType.getComponentType(), 0);
    }

    <P> Method findMethod(Class<?> type, String name, Class<?> baseType, Class<P> parameterType) {
        Method[] methods = type.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(name)) {
            	if(parameterType==null || (method.getParameterTypes().length==1 && method.getParameterTypes()[0].equals(parameterType))) {
            		return method;
            	}
            }
        }
        if (type.equals(Object.class)) {
            // TODO better
            throw new IllegalArgumentException("Unable to find method for " + name + " @ " + baseType.getName());
        }
        return findMethod(type.getSuperclass(), name, type, parameterType);
    }

    Method findGetter(Object target, String propertyCapitalized) {
        Class<? extends Object> targetClass = target.getClass();
        
        if (proxifier.isProxy(target)) {
            targetClass = targetClass.getSuperclass();
        }
        
        return new Mirror().on(targetClass).reflect().method("get" + propertyCapitalized).withoutArgs();
    }

	Method findSetter(Object target, String propertyCapitalized, Class<? extends Object> argument) {
		Class<? extends Object> targetClass = target.getClass();
		
        if (proxifier.isProxy(target)) {
            targetClass = targetClass.getSuperclass();
        }
		
		return new Mirror().on(targetClass).reflect().method("set" + propertyCapitalized).withArgs(argument);
	}


}
