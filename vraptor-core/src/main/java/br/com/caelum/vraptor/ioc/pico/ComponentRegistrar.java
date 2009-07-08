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
package br.com.caelum.vraptor.ioc.pico;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.Stereotype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * <p>Prepares all classes with meta-annotation @Stereotype to be used as VRaptor components. It means that any
 * annotation which in turn is annotated with @Stereotype, serves to mark a component.</p>
 * <p>The most common marker annotation for components is @Component (which is annotated with @Stereotype). Others that
 * are also common include @Intercepts and @Convert.</p>
 *
 * @author Paulo Silveira
 * @author Fabio Kung
 */
@ApplicationScoped
public class ComponentRegistrar implements Registrar {
    private final Logger logger = LoggerFactory.getLogger(ComponentRegistrar.class);
    private List<Class<? extends Annotation>> bundledStereotypes =
            Arrays.asList(Component.class, Intercepts.class, Convert.class, Resource.class);

    private final ComponentRegistry registry;

    public ComponentRegistrar(ComponentRegistry registry) {
        this.registry = registry;
    }

    public void registerFrom(Scanner scanner) {
        logger.info("Registering all classes with stereotyped annotations (annotations annotated with @Stereotype)");
        for (Class<? extends Annotation> bundledStereotype : bundledStereotypes) {
            Collection<Class<?>> componentTypes = scanner.getTypesWithAnnotation(bundledStereotype);
            for (Class<?> componentType : componentTypes) {
                logger.debug("found component: " + componentType + ", annotated with: " + bundledStereotype);
                deepRegister(componentType, componentType, new HashSet<Class<?>>());
            }
        }

        Collection<Class<?>> componentTypes = scanner.getTypesWithMetaAnnotation(Stereotype.class);
        for (Class<?> componentType : componentTypes) {
            logger.debug("found component: " + componentType);
            deepRegister(componentType, componentType, new HashSet<Class<?>>());
        }
    }

    private void deepRegister(Class<?> required, Class<?> component, Set<Class<?>> registeredKeys) {
        if (required == null || required.equals(Object.class))
            return;

        if (!registeredKeys.contains(required)) {
            registeredKeys.add(required);
            registry.register(required, component);
        }

        for (Class<?> c : required.getInterfaces()) {
            deepRegister(c, component, registeredKeys);
        }

        deepRegister(required.getSuperclass(), component, registeredKeys);
    }
}
