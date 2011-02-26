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

import org.springframework.beans.factory.FactoryBean;

import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.ComponentFactoryIntrospector;
import br.com.caelum.vraptor.ioc.Container;

/**
 * @author: Fabio Kung
 */
@SuppressWarnings({ "rawtypes" })
public class ComponentFactoryBean<T extends ComponentFactory<Object>> implements FactoryBean {

    private Container container;

    private Class<T> factoryType;
    private Class<?> targetType;

    public ComponentFactoryBean(Container container, Class<T> factoryType) {
        this.container = container;
        this.factoryType = factoryType;
        this.targetType = new ComponentFactoryIntrospector().targetTypeForComponentFactory(factoryType);
    }

    public Object getObject() {
        return container.instanceFor(factoryType).getInstance();
    }

    public Class<?> getObjectType() {
        return targetType;
    }

    public boolean isSingleton() {
        return false;
    }

}
