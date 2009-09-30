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

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Implementations of this interface define several strategies for classpath scanning.
 *
 * @author Fabio Kung
 */
public interface Scanner {

	<T> Collection<Class<? extends T>> getSubtypesOf(Class<T> type);

    Collection<Class<?>> getTypesWithAnnotation(Class<? extends Annotation> annotationType);

    Collection<Class<?>> getTypesWithMetaAnnotation(Class<? extends Annotation> metaAnnotationType);

    <T> Collection<Class<? extends T>> getSubtypesOfWithAnnotation(Class<T> requiredType,
                                                                   Class<? extends Annotation> annotationType);

    <T> Collection<Class<? extends T>> getSubtypesOfWithMetaAnnotation(Class<T> requiredType,
                                                                       Class<? extends Annotation> annotationType);
}
