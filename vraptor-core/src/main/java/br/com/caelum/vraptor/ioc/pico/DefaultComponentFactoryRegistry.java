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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.ComponentFactoryIntrospector;
import br.com.caelum.vraptor.ioc.SessionScoped;

/**
 * Registry to all ComponentRegistry classes
 *
 * @author Sérgio Lopes
 */
@ApplicationScoped
public class DefaultComponentFactoryRegistry implements ComponentFactoryRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DefaultComponentFactoryRegistry.class);

    /* maps from targetClass to componentFactoryClass */
    private final Map<Class<?>, Class<? extends ComponentFactory>> applicationScoped =
            new HashMap<Class<?>, Class<? extends ComponentFactory>>();
    private final Map<Class<?>, Class<? extends ComponentFactory>> sessionScoped =
            new HashMap<Class<?>, Class<? extends ComponentFactory>>();
    private final Map<Class<?>, Class<? extends ComponentFactory>> requestScoped =
            new HashMap<Class<?>, Class<? extends ComponentFactory>>();

    private ComponentFactoryIntrospector componentFactoryIntrospector = new ComponentFactoryIntrospector();

    public void register(Class<? extends ComponentFactory> componentFactoryClass) {
        Class<?> targetType = componentFactoryIntrospector.targetTypeForComponentFactory(componentFactoryClass);

        if (componentFactoryClass.isAnnotationPresent(ApplicationScoped.class)) {
			logger.debug("Registering a ComponentFactory for {} in app scope", targetType.getName());
            applicationScoped.put(targetType, componentFactoryClass);
        } else if (componentFactoryClass.isAnnotationPresent(SessionScoped.class)) {
			logger.debug("Registering a ComponentFactory for {} in session scope", targetType.getName());
            sessionScoped.put(targetType, componentFactoryClass);
        } else { // @RequestScoped
			logger.debug("Registering a ComponentFactory for {} in request scope", targetType.getName());
            requestScoped.put(targetType, componentFactoryClass);
        }
    }

    public Map<Class<?>, Class<? extends ComponentFactory>> getApplicationMap() {
        return applicationScoped;
    }

    public Map<Class<?>, Class<? extends ComponentFactory>> getSessionMap() {
        return sessionScoped;
    }

    public Map<Class<?>, Class<? extends ComponentFactory>> getRequestMap() {
        return requestScoped;
    }
}
