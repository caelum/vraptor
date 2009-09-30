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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.ioc.Stereotype;

/**
 * @author Fabio Kung
 */
class ComponentTypeFilter implements TypeFilter {

    private final Collection<Class<? extends Annotation>> annotationTypes;

    public ComponentTypeFilter() {
        this.annotationTypes = new ArrayList<Class<? extends Annotation>>();
        for (Class<? extends Annotation> stereotype : BaseComponents.getStereotypes()) {
			this.annotationTypes.add(stereotype);
		}
    }

    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        for (Class<? extends Annotation> annotationType : annotationTypes) {
            if (metadata.hasAnnotation(annotationType.getName())) {
                return true;
            }
        }
        return metadata.hasMetaAnnotation(Stereotype.class.getName());
    }
}
