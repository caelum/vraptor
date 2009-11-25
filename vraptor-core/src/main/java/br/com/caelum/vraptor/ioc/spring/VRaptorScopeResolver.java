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

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopeMetadata;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.context.WebApplicationContext;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.PrototypeScoped;
import br.com.caelum.vraptor.ioc.RequestScoped;
import br.com.caelum.vraptor.ioc.SessionScoped;

/**
 * @author Fabio Kung
 */
class VRaptorScopeResolver implements ScopeMetadataResolver {
    private final Map<String, String> scopes = new LinkedHashMap<String, String>();

    public VRaptorScopeResolver() {
        scopes.put(RequestScoped.class.getName(), WebApplicationContext.SCOPE_REQUEST);
        scopes.put(SessionScoped.class.getName(), WebApplicationContext.SCOPE_SESSION);
        scopes.put(ApplicationScoped.class.getName(), BeanDefinition.SCOPE_SINGLETON);
        scopes.put(PrototypeScoped.class.getName(), BeanDefinition.SCOPE_PROTOTYPE);
    }

    public ScopeMetadata resolveScopeMetadata(BeanDefinition definition) {
        AnnotationMetadata metadata = ((AnnotatedBeanDefinition) definition).getMetadata();
        for (Map.Entry<String, String> scope : scopes.entrySet()) {
            if (metadata.hasAnnotation(scope.getKey())) {
                ScopeMetadata scopeMetadata = new ScopeMetadata();
                scopeMetadata.setScopeName(scope.getValue());
                return scopeMetadata;
            }
        }
        return requestScopeAsDefault();
    }

    private ScopeMetadata requestScopeAsDefault() {
        ScopeMetadata singleton = new ScopeMetadata();
        singleton.setScopeName(WebApplicationContext.SCOPE_REQUEST);
        return singleton;
    }
}
