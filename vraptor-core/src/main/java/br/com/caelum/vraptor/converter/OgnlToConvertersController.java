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
package br.com.caelum.vraptor.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import ognl.TypeConverter;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.validator.ValidationMessage;

@RequestScoped
public class OgnlToConvertersController implements TypeConverter {

    private final Converters converters;
	private final ResourceBundle bundle;
	private final List<ValidationMessage> errors;

    public OgnlToConvertersController(Converters converters, List<ValidationMessage> errors, ResourceBundle bundle) {
        this.converters = converters;
		this.errors = errors;
		this.bundle = bundle;
    }

    @SuppressWarnings("unchecked")
    public Object convertValue(Map context, Object target, Member member, String propertyName, Object value,
            Class toType) {
        Type genericType = genericTypeToConvert(target, member);
        Class type = rawTypeOf(genericType);
        Container container = (Container) context.get(Container.class);
        Converter<?> converter = converters.to(type, container);
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

    @SuppressWarnings("unchecked")
    public static Class rawTypeOf(Type genericType) {
        if (genericType instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) genericType).getRawType();
        }
        return (Class) genericType;
    }

    private Type extractArrayType(Object target) {
        return target.getClass().getComponentType();
    }

    private Type extractFieldType(Member member) {
        return ((Field)member).getGenericType();
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
