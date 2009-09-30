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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.Stereotype;

/**
 * Prepares all components implementing br.com.caelum.vraptor.ioc.ComponentFactory to be used as Pico ComponentAdapters.
 *
 * @author Sérgio Lopes
 * @author Fabio Kung
 */
@ApplicationScoped
public class ComponentFactoryRegistrar implements Registrar {

    private final Logger logger = LoggerFactory.getLogger(ComponentFactoryRegistrar.class);
    private final ComponentFactoryRegistry registry;

    public ComponentFactoryRegistrar(ComponentFactoryRegistry registry) {
        this.registry = registry;
    }

    public void registerFrom(Scanner scanner) {
        logger.info("Registering all components that implement ComponentFactory as Pico ComponentAdapters");
        Collection<Class<? extends ComponentFactory>> factoryTypes =
                scanner.getSubtypesOfWithMetaAnnotation(ComponentFactory.class, Stereotype.class);

        for (Class<? extends ComponentFactory> factoryType : factoryTypes) {
            logger.debug("ComponentFactory found: " + factoryType);
            registry.register(factoryType);
        }
    }

}
