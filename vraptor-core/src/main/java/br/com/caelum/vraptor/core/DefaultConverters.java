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

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;

@ApplicationScoped
public final class DefaultConverters implements Converters {

    private final LinkedList<Class<? extends Converter>> classes;
    private final Logger logger = LoggerFactory.getLogger(DefaultConverters.class);

    public DefaultConverters() {
        this.classes = new LinkedList<Class<? extends Converter>>();
        logger.info("Registering bundled converters");
        for (Class<? extends Converter<?>> converterType : BaseComponents.getBundledConverters()) {
            logger.debug("bundled converter to be registered: " + converterType);
            register(converterType);
        }
    }

    public void register(Class<? extends Converter<?>> converterClass) {
        if (!converterClass.isAnnotationPresent(Convert.class)) {
            throw new VRaptorException("The converter type " + converterClass.getName()
                    + " should have the Convert annotation");
        }
        classes.addFirst(converterClass);
    }

    public Converter to(Class<?> clazz, Container container) {
        for (Class<? extends Converter> converterType : classes) {
            Class boundType = converterType.getAnnotation(Convert.class).value();
            if (boundType.isAssignableFrom(clazz)) {
                return container.instanceFor(converterType);
            }
        }
        throw new VRaptorException("Unable to find converter for " + clazz.getName());
    }

}
