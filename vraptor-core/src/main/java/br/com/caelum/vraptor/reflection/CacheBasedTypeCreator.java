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

package br.com.caelum.vraptor.reflection;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.http.AbstractTypeCreator;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Cacheable;
import br.com.caelum.vraptor.resource.ResourceMethod;

/**
 * A type creator which caches its classes so it doesnt keep generating classes
 * between every call.
 *
 * @author Guilherme Silveira
 */

@ApplicationScoped
public class CacheBasedTypeCreator extends AbstractTypeCreator {

    private static final Logger logger = LoggerFactory.getLogger(CacheBasedTypeCreator.class);
    private final Map<Method, Class<?>> cache = new HashMap<Method, Class<?>>();
    private final TypeCreator creator;

    public CacheBasedTypeCreator(@Cacheable TypeCreator creator, ParameterNameProvider provider) {
    	super(provider);
        this.creator = creator;
    }

    public Class<?> typeFor(ResourceMethod method) {
        if (!cache.containsKey(method.getMethod())) {
            cache.put(method.getMethod(), creator.typeFor(method));
            logger.debug("cached generic type for method " + method);
        }
        return cache.get(method.getMethod());
    }

}
