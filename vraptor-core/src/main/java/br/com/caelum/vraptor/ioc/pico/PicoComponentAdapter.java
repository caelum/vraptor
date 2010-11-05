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

import java.lang.reflect.Type;

import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.AbstractAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.ComponentFactory;

/**
 * Pico's ComponentAdapter wrapping VRaptor's ComponentFactory
 *
 * @author Sérgio Lopes
 */
@SuppressWarnings({"serial"})
public class PicoComponentAdapter extends AbstractAdapter {

    private static final Logger logger = LoggerFactory.getLogger(PicoComponentAdapter.class);

    private final Class<?> targetType;
    private final Class<? extends ComponentFactory> componentFactoryClass;

    public PicoComponentAdapter(Class<?> targetType, Class<? extends ComponentFactory> componentFactoryClass) {
        super(targetType, targetType);

        logger.debug("New adapter for {}", componentFactoryClass.getName());

        this.targetType = targetType;
        this.componentFactoryClass = componentFactoryClass;
    }

    public Object getComponentInstance(PicoContainer pico, Type type)
            throws PicoCompositionException {

		logger.debug("Providing {} instance via {}", targetType.getName(), componentFactoryClass.getName());

        ComponentFactory<?> componentFactory = pico.getComponent(componentFactoryClass);
        return componentFactory.getInstance();
    }

    public String getDescriptor() {
        return "Adapter for " + targetType.getName();
    }

    public void verify(PicoContainer pico) throws PicoCompositionException {
    }

}
