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
package br.com.caelum.vraptor.vraptor2;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vraptor.annotations.Logic;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * Looks up for the Logic annotation on the method, if its found and containing
 * the parameters value, its value is used. Otherwise, fallsback to the
 * delegated provider. The default delegate provider is the paranamer based.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class LogicAnnotationWithParanamerParameterNameProvider implements ParameterNameProvider {

	private static final Logger logger = LoggerFactory.getLogger(LogicAnnotationWithParanamerParameterNameProvider.class);

    private final ParameterNameProvider delegate;

    public LogicAnnotationWithParanamerParameterNameProvider() {
        this(new ParanamerNameProvider());
    }

    public LogicAnnotationWithParanamerParameterNameProvider(ParameterNameProvider delegate) {
        this.delegate = delegate;
    }

    public String[] parameterNamesFor(AccessibleObject method) {
        if (method.isAnnotationPresent(Logic.class)) {
            return method.getAnnotation(Logic.class).parameters();
        }
        try {
			return delegate.parameterNamesFor(method);
		} catch (IllegalStateException e) {
			logger.warn("VRaptor2 Component method without parameter names! Returning null array {}", method);
			return new String[((Method) method).getParameterTypes().length];
		}
    }

}
