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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;

import br.com.caelum.vraptor.ioc.AbstractComponentRegistry;
import br.com.caelum.vraptor.ioc.Container;

/**
 * @author Fabio Kung
 */
public class SpringBasedContainer extends AbstractComponentRegistry implements Container {

    private VRaptorApplicationContext applicationContext;

    private final List<Class<?>> toRegister = new ArrayList<Class<?>>();

    public SpringBasedContainer(ApplicationContext parentContext, String... basePackages) {
		String[] packages = {"br.com.caelum.vraptor"};
        if (basePackages.length > 0) {
            packages = basePackages;
        }
        applicationContext = new VRaptorApplicationContext(this, packages);
        applicationContext.setParent(parentContext);
    }

    public void register(Class<?> requiredType, Class<?> componentType) {
    	if (applicationContext.isActive()) {
			applicationContext.register(componentType);
		} else {
			toRegister.add(componentType);
		}
    }

    public List<Class<?>> getToRegister() {
		return toRegister;
	}

    public <T> T instanceFor(Class<T> type) {
        return applicationContext.getBean(type);
    }

    public void start(ServletContext context) {
        applicationContext.setServletContext(context);
        applicationContext.refresh();
        applicationContext.start();
    }

    public void stop() {
        applicationContext.stop();
        applicationContext.destroy();
        applicationContext = null;
    }

}
