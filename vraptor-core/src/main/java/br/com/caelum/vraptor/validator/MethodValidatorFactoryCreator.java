/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.validator;

import static java.util.Arrays.asList;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.proxy.InstanceCreator;

/**
 * Bring up Method Validation factory. This class builds the {@link ValidatorFactory} factory once when
 * application starts. This class only works with Bean Validation 1.1 (for 1.0 use
 * {@link ValidatorFactoryCreator} instead. WARN: Method validation is beta, and is subject to change.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.5.2-SNAPSHOT
 */
@ApplicationScoped
public class MethodValidatorFactoryCreator
    implements ComponentFactory<ValidatorFactory> {
    
    private static final Logger logger = LoggerFactory.getLogger(MethodValidatorFactoryCreator.class);
    private static final List<Method> OBJECT_METHODS = asList(Object.class.getDeclaredMethods());

    private ValidatorFactory instance;
    private final ParameterNameProvider nameProvider;
    private final Container container;
    
    public MethodValidatorFactoryCreator(ParameterNameProvider nameProvider, Container container) {
        this.nameProvider = nameProvider;
        this.container = container;
    }
    
    @PostConstruct
    public void buildFactory() {
        instance = Validation.byDefaultProvider().configure()
            .parameterNameProvider(new CustomParameterNameProvider(nameProvider))
            .constraintValidatorFactory(new CustomConstraintValidatorFactory(container))
            .buildValidatorFactory();
        
        logger.debug("Initializing Bean Validation (1.1 supported)");
    }
    
    @PreDestroy
    public void close() {
        instance.close();
    }

    public ValidatorFactory getInstance() {
        if (instance == null) { //pico don't call PostConstruct
            buildFactory();
        }
        return instance;
    }

    /**
     * Allow vraptor to use paranamer to discovery method parameter names.
     * @author Otávio Scherer Garcia
     * @since 3.5.2-SNAPSHOT
     */
    class CustomParameterNameProvider
        implements javax.validation.ParameterNameProvider {
    
        private final ParameterNameProvider nameProvider;
        
        public CustomParameterNameProvider(ParameterNameProvider nameProvider) {
            this.nameProvider = nameProvider;
        }

        /**
         * Returns an empty list of parameter names, since we don't validate 
         * constructors.
         */
        public List<String> getParameterNames(Constructor<?> constructor) {
            return emptyParameters(constructor.getParameterTypes().length);
        }

        /**
         * Returns the parameter names for the method, skiping if method is inherited 
         * from object.
         */
        public List<String> getParameterNames(Method method) {
            if (OBJECT_METHODS.contains(method)) {
                return emptyParameters(method.getParameterTypes().length);
            }
            
            return asList(nameProvider.parameterNamesFor(method));
        }
        
        private List<String> emptyParameters(int length) {
            return asList(new String[length]);
        }
    }

    /**
     * Create a custom {@link ConstraintValidatorFactory} to allow users to use constraints that uses
     * components.
     * @author Otávio Scherer Garcia
     * @since 3.5.2-SNAPSHOT
     */
    class CustomConstraintValidatorFactory
        implements ConstraintValidatorFactory {

        private final Container container;
    
        public CustomConstraintValidatorFactory(Container container) {
            this.container = container;
        }
    
        public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
            if (container.canProvide(key)) {
                logger.debug("we can provide instance for ConstraintValidator {}", key);
                return container.instanceFor(key);
            }
            
            return container.instanceFor(InstanceCreator.class).instanceFor(key);
        }
    
        public void releaseInstance(ConstraintValidator<?, ?> key) {
            // we don't need this
        }
    }
}
