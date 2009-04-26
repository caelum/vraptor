/***
 * 
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
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
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.vraptor2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vraptor.annotations.In;
import org.vraptor.annotations.Logic;
import org.vraptor.annotations.Out;
import org.vraptor.annotations.Parameter;
import org.vraptor.plugin.hibernate.Validate;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.resource.DefaultMethodLookupBuilder;
import br.com.caelum.vraptor.resource.MethodLookupBuilder;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceAndMethodLookup;

/**
 * A vraptor 2 compatible method lookup builder.
 * 
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class VRaptor2MethodLookupBuilder implements MethodLookupBuilder {
    
    private static final Logger logger = LoggerFactory.getLogger(VRaptor2MethodLookupBuilder.class);
    private final DefaultMethodLookupBuilder newBuilder;
    
    public VRaptor2MethodLookupBuilder() {
        this.newBuilder = new DefaultMethodLookupBuilder();
    }

    public ResourceAndMethodLookup lookupFor(Resource resource) {
        Class<?> type = resource.getType();
        if(Info.isOldComponent(resource)) {
            logger.warn("Old component found, remember to migrate to vraptor3: " + type.getName());
        }
        parse(type, type);
        return new VRaptor2MethodLookup(resource);
    }

    private void parse(Class<?> type, Class<?> originalType) {
        if (type.equals(Object.class)) {
            return;
        }
        for (Class<? extends Annotation> annotation : new Class[] { Out.class, In.class, Parameter.class }) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotation)) {
                    logger.error("Field " + field.getName() + " from " + originalType.getName() + " is annotated with "
                            + annotation.getName() + " but is not supported by VRaptor3! Read the migration guide.");
                }
            }
        }
        for (Method method : type.getDeclaredMethods()) {
            for (Class<? extends Annotation> annotation : new Class[] { Out.class, In.class }) {
                if (method.isAnnotationPresent(annotation)) {
                    logger.error("Method " + method.getName() + " from " + originalType.getName()
                            + " is annotated with " + annotation.getName()
                            + " but is not supported by VRaptor3! Read the migration guide.");
                }
            }
            if (method.isAnnotationPresent(Logic.class)) {
                logger.warn("Method " + method.getName() + " from " + originalType.getName()
                        + " is annotated with @Logic. Although its supported, we suggest you to migrate to @Path.");
            }
            if (method.isAnnotationPresent(Validate.class)) {
                Validate validate = method.getAnnotation(Validate.class);
                if (validate.fields().length != 0) {
                    logger.error("Method " + method.getName() + " from " + originalType.getName()
                            + " is annotated with @Validate with fields. This is not supported.");
                }
            }
        }
        parse(type.getSuperclass(), type);
    }

    public String urlFor(Class<?> type, Method method, Object... params) {
        return newBuilder.urlFor(type, method, params);
    }

}
