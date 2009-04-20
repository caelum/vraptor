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
public class SignatureConverter {

    private static Map<Class<?>, String> WRAPPERS = new HashMap<Class<?>, String>();
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
