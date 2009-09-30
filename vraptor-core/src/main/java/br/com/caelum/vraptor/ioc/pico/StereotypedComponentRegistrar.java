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

package br.com.caelum.vraptor.ioc.pico;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Stereotype;
import br.com.caelum.vraptor.ioc.StereotypeHandler;

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
public class StereotypedComponentRegistrar implements Registrar {
    private final Logger logger = LoggerFactory.getLogger(StereotypedComponentRegistrar.class);

    private final ComponentRegistry registry;
	private final List<StereotypeHandler> handlers;

    public StereotypedComponentRegistrar(ComponentRegistry registry, List<StereotypeHandler> handlers) {
        this.registry = registry;
		this.handlers = handlers;
    }

    public void registerFrom(Scanner scanner) {
        logger.info("Registering all classes with stereotyped annotations (annotations annotated with @Stereotype)");

        for (StereotypeHandler handler : handlers) {
            Collection<Class<?>> componentTypes = scanner.getTypesWithAnnotation(handler.stereotype());
            for (Class<?> componentType : componentTypes) {
                logger.debug("found component: " + componentType + ", annotated with: " + handler.stereotype());
                handler.handle(componentType);
                registry.deepRegister(componentType);
            }
		}

        Collection<Class<?>> componentTypes = scanner.getTypesWithMetaAnnotation(Stereotype.class);
        for (Class<?> componentType : componentTypes) {
            logger.debug("found component: " + componentType);
            registry.deepRegister(componentType);
        }
    }

}
