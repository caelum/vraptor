/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility methods to handle with reflection
 * 
 * @author Nykolas Lima
 *
 */
public class ReflectionUtils {
	@SuppressWarnings("serial")
	private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPERS = new HashMap<Class<?>, Class<?>>() {{
		put(boolean.class, Boolean.class);
		put(byte.class, Byte.class);
		put(char.class, Character.class);
		put(double.class, Double.class);
	    put(float.class, Float.class);
	    put(int.class, Integer.class);
	    put(long.class, Long.class);
	    put(short.class, Short.class);
	}};
	
	public static Method findMethodWithName(Class<?> type, String name, List<Object> args) {
		return findMethodWithName(type, name, getClasses(args));
	}
	
	public static Method findMethodWithName(Class<?> type, String name, Class<?>[] args) {
		if (args != null && args.length > 0)
			return findMethodWithNameAndArgs(type, name, args);
		else
			return findMethodWithNameAndNoArgs(type, name);
	}
	
	public static Method findMethodWithNameAndArgs(Class<?> type, String name, Class<?>[] args) {
		for (Method method : type.getDeclaredMethods()) {
			if (!method.isBridge() && method.getName().equals(name) && isParamsEquals(method.getParameterTypes(), args)) {
				return method;
			}
		}
		if (type.getSuperclass().equals(Object.class)) {
			throw new IllegalArgumentException("There are no methods on " + type + " named " + name);
		}
		return findMethodWithNameAndNoArgs(type.getSuperclass(), name);
	}
	
	public static Method findMethodWithNameAndNoArgs(Class<?> type, String name) {
		for (Method method : type.getDeclaredMethods()) {
			if (!method.isBridge() && method.getName().equals(name)) {
				return method;
			}
		}
		if (type.getSuperclass().equals(Object.class)) {
			throw new IllegalArgumentException("There are no methods on " + type + " named " + name);
		}
		return findMethodWithNameAndNoArgs(type.getSuperclass(), name);
	}
	
	public static boolean isParamsEquals(Class<?>[] params1, Class<?>[] params2) {
		if (params1.length != params2.length) return false;
		for (int i = 0; i < params1.length; i++) {
			if(params1[i].isPrimitive()) {
				if (!PRIMITIVE_TO_WRAPPERS.get(params1[i]).isAssignableFrom(params2[i])) return false;
			} else {
				if (!params1[i].isAssignableFrom(params2[i])) return false; 
			}
		}
		return true;
	}
	
	public static Class<?>[] getClasses(List<Object> params) {
		Class<?>[] classes = new Class<?>[params.size()];
		for(int i = 0; i < params.size(); i ++) {
			classes[i] = params.get(i).getClass();
		}
		return classes;
   }
}
