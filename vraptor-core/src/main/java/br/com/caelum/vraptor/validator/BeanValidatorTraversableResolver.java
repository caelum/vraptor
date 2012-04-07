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

import java.lang.annotation.ElementType;

import javax.validation.Path;
import javax.validation.TraversableResolver;

/**
 * A custom {@link TraversableResolver} to bootstrap Bean Validation avoiding classpath problems with Hibernate
 * Validator 4 and JPA2. See https://forum.hibernate.org/viewtopic.php?p=2422115#p2422115 for more details.
 * 
 * @author Otávio Scherer Garcia
 * @version 3.1.3
 */
public class BeanValidatorTraversableResolver
    implements TraversableResolver {

    public boolean isReachable(Object traversableObject, Path.Node traversableProperty, Class<?> rootBeanType,
            Path pathToTraversableObject, ElementType elementType) {
        return true;
    }

    public boolean isCascadable(Object object, Path.Node node, Class<?> clazz, Path path, ElementType elementType) {
        return true;
    }
}