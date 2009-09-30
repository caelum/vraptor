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
package br.com.caelum.vraptor.http;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class DefaultParameterNameProvider implements ParameterNameProvider{

    private String extractName(ParameterizedType type) {
        Type raw = type.getRawType();
        String name = extractName((Class<?>) raw) + "Of";
        Type[] types = type.getActualTypeArguments();
        for(Type t : types) {
            name += extractName((Class<?>) t);
        }
        return name;
    }

    private String extractName(Class<?> type) {
        if(type.isArray()) {
            return type.getComponentType().getSimpleName();
        }
        return type.getSimpleName();
    }

    private String nameFor(Type type) {
        if(type instanceof ParameterizedType) {
            return extractName((ParameterizedType) type);
        }
        return extractName((Class<?>) type);
    }

    public String[] parameterNamesFor(Method method) {
        Type[] parameterTypes = method.getGenericParameterTypes();
        String[] names = new String[parameterTypes.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = nameFor(parameterTypes[i]);
        }
        return names;
    }

}
