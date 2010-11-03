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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
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

import br.com.caelum.vraptor.ioc.Container;

/**
 * Capable of instantiating generic interfaces and custom types.
 *
 * @author Guilherme Silveira
 */
class GenericNullHandler {

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
    <T> T instantiate(Class<T> baseType, Container container) throws InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
    	if (baseType.isArray()) {
    		return baseType.cast(Array.newInstance(baseType.getComponentType(), 0));
    	}
        Object instance;
        Class<?> typeToInstantiate = baseType;
        if (baseType.isInterface() || Modifier.isAbstract(baseType.getModifiers())) {
            if (!CONCRETE_TYPES.containsKey(baseType)) {
                // TODO better
                throw new IllegalArgumentException("Vraptor does not support this interface or abstract type: "
                        + typeToInstantiate.getName());
            }
            typeToInstantiate = CONCRETE_TYPES.get(baseType);
        }
        Constructor<?> constructor = typeToInstantiate.getDeclaredConstructor();
        constructor.setAccessible(true);
		instance = constructor.newInstance();
        if(Collection.class.isAssignableFrom(typeToInstantiate)) {
	        EmptyElementsRemoval removal = container.instanceFor(EmptyElementsRemoval.class);
        	removal.add((Collection)instance);
        }

        return (T) instance;
    }

}
