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
package br.com.caelum.vraptor.core;

import java.util.Iterator;
import java.util.LinkedList;

import javax.annotation.PostConstruct;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.converter.BooleanConverter;
import br.com.caelum.vraptor.converter.ByteConverter;
import br.com.caelum.vraptor.converter.DoubleConverter;
import br.com.caelum.vraptor.converter.EnumConverter;
import br.com.caelum.vraptor.converter.FloatConverter;
import br.com.caelum.vraptor.converter.IntegerConverter;
import br.com.caelum.vraptor.converter.LocaleBasedCalendarConverter;
import br.com.caelum.vraptor.converter.LocaleBasedDateConverter;
import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.converter.PrimitiveBooleanConverter;
import br.com.caelum.vraptor.converter.PrimitiveByteConverter;
import br.com.caelum.vraptor.converter.PrimitiveDoubleConverter;
import br.com.caelum.vraptor.converter.PrimitiveFloatConverter;
import br.com.caelum.vraptor.converter.PrimitiveIntConverter;
import br.com.caelum.vraptor.converter.PrimitiveLongConverter;
import br.com.caelum.vraptor.converter.PrimitiveShortConverter;
import br.com.caelum.vraptor.converter.ShortConverter;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFileConverter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;

@SuppressWarnings("unchecked")
@ApplicationScoped
public final class DefaultConverters implements Converters {

    private final LinkedList<Class<? extends Converter>> types;

    public static final Class<? extends Converter<?>>[] DEFAULTS = new Class[] { PrimitiveIntConverter.class,
            PrimitiveLongConverter.class, PrimitiveShortConverter.class, PrimitiveByteConverter.class,
            PrimitiveDoubleConverter.class, PrimitiveFloatConverter.class, PrimitiveBooleanConverter.class,
            IntegerConverter.class, LongConverter.class, ShortConverter.class, ByteConverter.class,
            DoubleConverter.class, FloatConverter.class, BooleanConverter.class, LocaleBasedCalendarConverter.class,
            LocaleBasedDateConverter.class, EnumConverter.class, UploadedFileConverter.class };

    private final ComponentRegistry container;

    public DefaultConverters(ComponentRegistry container) {
        this.container = container;
        this.types = new LinkedList<Class<? extends Converter>>();
    }
    
    @PostConstruct
    public void init() {
        for (Class<? extends Converter<?>> type : DEFAULTS) {
            register(type);
        }
    }

    protected void register(Class<? extends Converter> converterType) {
        if (!converterType.isAnnotationPresent(Convert.class)) {
            // TODO is this the correct one?
            throw new IllegalArgumentException("The converter type " + converterType.getName()
                    + " should have the Convert annotation");
        }
        types.addFirst(converterType);
        container.register(converterType, converterType);
    }

    @SuppressWarnings("unchecked")
    public Converter to(Class<?> type, Container container) {
        for (Class<? extends Converter> converterType : types) {
            Class boundType = converterType.getAnnotation(Convert.class).value();
            if (boundType.isAssignableFrom(type)) {
                return container.instanceFor(converterType);
            }
        }
        // TODO improve
        throw new IllegalArgumentException("Unable to find converter for " + type.getName());
    }

}
