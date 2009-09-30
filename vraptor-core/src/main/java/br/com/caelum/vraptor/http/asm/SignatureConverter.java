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

package br.com.caelum.vraptor.http.asm;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Generates the signature String based on a class.<br>
 * Currently supports generics, arrays, primitives and custom types.
 * 
 * @author Guilherme Silveira
 */
class SignatureConverter {

    private static final Map<Class<?>, String> WRAPPERS = new HashMap<Class<?>, String>();
    static {
        WRAPPERS.put(boolean.class, "Z");
        WRAPPERS.put(int.class, "I");
        WRAPPERS.put(short.class, "S");
        WRAPPERS.put(long.class, "J");
        WRAPPERS.put(double.class, "D");
        WRAPPERS.put(float.class, "F");
        WRAPPERS.put(byte.class, "B");
        WRAPPERS.put(char.class, "C");
    }

    String wrapperFor(Class<?> type) {
        return WRAPPERS.get(type);
    }

    String extractTypeDefinition(Class<?> type) {
        if (type.isArray()) {
            return "[" + extractTypeDefinition(type.getComponentType());
        }
        if (type.isPrimitive()) {
            return WRAPPERS.get(type);
        }
        return 'L' + type.getName().replace('.', '/') + ';';
    }

    @SuppressWarnings("unchecked")
    String extractTypeDefinition(ParameterizedType type) {
        Type raw = type.getRawType();
        String name = extractTypeDefinition((Class) raw);
        name = name.substring(0, name.length() - 1) + "<";
        Type[] types = type.getActualTypeArguments();
        for (Type t : types) {
            name += extractTypeDefinition((Class) t);
        }
        return name + ">;";
    }

}
