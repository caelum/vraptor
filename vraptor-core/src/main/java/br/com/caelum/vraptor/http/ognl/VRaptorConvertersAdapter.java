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
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.ResourceBundle;

import ognl.TypeConverter;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.RequestScoped;

@RequestScoped
public class VRaptorConvertersAdapter implements TypeConverter {

    private final Converters converters;
    private final ResourceBundle bundle;

    public VRaptorConvertersAdapter(Converters converters, ResourceBundle bundle) {
        this.converters = converters;
        this.bundle = bundle;
    }

    public Object convertValue(Map context, Object target, Member member, String propertyName, Object value,
            Class toType) {
        Type genericType = genericTypeToConvert(target, member);
        Class type = rawTypeOf(genericType);
        if (type.isArray() && !value.getClass().isArray()) {
        	Class arrayType = type.getComponentType();
        	Object array = Array.newInstance(arrayType, 1);
        	Array.set(array, 0, convert(value, arrayType));
        	return array;
        }
        return convert(value, type);
    }

	Object convert(Object value, Class type) {
		Converter<?> converter = converters.to(type);
        if (converter == null) {
            // TODO better, validation error?
            throw new IllegalArgumentException("Cannot instantiate a converter for type " + type.getName());
        }
        return converter.convert((String) value, type, bundle);
	}

    private Type genericTypeToConvert(Object target, Member member) {
        if (member instanceof Field) {
            return extractFieldType(member);
        } else if (member instanceof Method) {
            return extractSetterMethodType(target, member);
        } else if (member == null && target.getClass().isArray()) {
            return extractArrayType(target);
        }
        // TODO better
        throw new IllegalArgumentException("Vraptor can only navigate through getter/setter methods, not " + member
                + " from " + target.getClass().getName());
    }

    private static Class rawTypeOf(Type genericType) {
        if (genericType instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) genericType).getRawType();
        }
        return (Class) genericType;
    }

    private Type extractArrayType(Object target) {
        return target.getClass().getComponentType();
    }

    private Type extractFieldType(Member member) {
        return ((Field) member).getGenericType();
    }

    private Type extractSetterMethodType(Object target, Member member) {
        Method method = (Method) member;
        Type[] parameterTypes = method.getGenericParameterTypes();
        if (parameterTypes.length != 1) {
            // TODO better
            throw new IllegalArgumentException("Vraptor can only navigate through setters with one parameter, not "
                    + member + " from " + target.getClass().getName());
        }
        return parameterTypes[0];
    }

}
