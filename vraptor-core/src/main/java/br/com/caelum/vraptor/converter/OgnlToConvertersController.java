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

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import ognl.TypeConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.ioc.Container;

public class OgnlToConvertersController implements TypeConverter {
    
    private final Converters converters;

    public OgnlToConvertersController(Converters converters) {
        this.converters = converters;
    }

    @SuppressWarnings("unchecked")
    public Object convertValue(Map context, Object target, Member member, String propertyName, Object value, Class toType) {
        if(!(member instanceof Method)) {
            throw new IllegalArgumentException("Vraptor can only navigate through getter/setter methods");
        }
        Method method = (Method) member;
        Type[] parameterTypes = method.getGenericParameterTypes();
        if(parameterTypes.length!=1) {
            throw new IllegalArgumentException("Vraptor can only navigate through setters with one parameter");
        }
        Type parameterType = parameterTypes[0];
        Class type;
        if(parameterType instanceof ParameterizedType) {
            type = (Class) ((ParameterizedType) parameterType).getRawType();
        } else {
            type = (Class) parameterType;
        }
        Container container = (Container) context.get(Container.class);
        return converters.to(type, container).convert((String) value, type);
    }

}
