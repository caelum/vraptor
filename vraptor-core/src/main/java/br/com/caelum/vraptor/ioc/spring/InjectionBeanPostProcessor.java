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

package br.com.caelum.vraptor.ioc.spring;

import java.lang.reflect.Constructor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;

/**
 * Enhances the default behavior from Spring, adding support to injection
 * through not annotated constructor, if there is only one.
 *
 * @author Fabio Kung
 */
class InjectionBeanPostProcessor extends AutowiredAnnotationBeanPostProcessor {


    //  in case we are required to change the injection annotation:
    //  public InjectionBeanPostProcessor() {
    //      this.setAutowiredAnnotationType(In.class);
    //  }

    @Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Constructor[] determineCandidateConstructors(Class beanClass, String beanName) throws BeansException {
        Constructor[] candidates = super.determineCandidateConstructors(beanClass, beanName);
        if (candidates == null) {
            Constructor constructor = checkIfThereIsOnlyOneNonDefaultConstructor(beanClass);
            if (constructor != null) {
                candidates = new Constructor[]{constructor};
            }
        }
        return candidates;
    }

    @SuppressWarnings({ "rawtypes" })
	private Constructor checkIfThereIsOnlyOneNonDefaultConstructor(Class beanClass) {
        Constructor[] constructors = beanClass.getDeclaredConstructors();
        if (constructors.length == 1) {
            if (constructors[0].getParameterTypes().length > 0) {
                return constructors[0];
            }
        }
        return null;
    }
}
