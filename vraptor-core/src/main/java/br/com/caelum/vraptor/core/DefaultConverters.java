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

package br.com.caelum.vraptor.core;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.TwoWayConverter;
import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;

@ApplicationScoped
public final class DefaultConverters implements Converters {

    private final LinkedList<Class<? extends Converter<?>>> classes;
    private final Logger logger = LoggerFactory.getLogger(DefaultConverters.class);
	private final Container container;

    public DefaultConverters(Container container) {
        this.container = container;
		this.classes = new LinkedList<Class<? extends Converter<?>>>();
        logger.info("Registering bundled converters");
        for (Class<? extends Converter<?>> converterType : BaseComponents.getBundledConverters()) {
            logger.debug("bundled converter to be registered: {}", converterType);
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

    public <T> Converter<T> to(Class<T> clazz) {
        if (!existsFor(clazz)) {
			throw new VRaptorException("Unable to find converter for " + clazz.getName());
		}
        return (Converter<T>) container.instanceFor(findConverterType(clazz));
    }

	private Class<? extends Converter<?>> findConverterType(Class<?> clazz) {
		for (Class<? extends Converter<?>> converterType : classes) {
            Class<?> boundType = converterType.getAnnotation(Convert.class).value();
            if (boundType.isAssignableFrom(clazz)) {
                return converterType;
            }
        }
		return null;
	}

	public boolean existsFor(Class<?> type) {
		return findConverterType(type) != null;
	}

	public boolean existsTwoWayFor(Class<?> type) {
		Class<? extends Converter<?>> found = findConverterType(type);
		return found != null && TwoWayConverter.class.isAssignableFrom(found);
	}

	public TwoWayConverter<?> twoWayConverterFor(Class<?> type) {
		if (!existsTwoWayFor(type)) {
			throw new VRaptorException("Unable to find two way converter for " + type.getName());
		}
        return (TwoWayConverter<?>) container.instanceFor(findConverterType(type));
	}

}
