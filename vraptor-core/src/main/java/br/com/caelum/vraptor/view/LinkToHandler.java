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
package br.com.caelum.vraptor.view;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.vidageek.mirror.dsl.Mirror;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.http.route.Router;

import com.google.common.collect.ForwardingMap;

/**
 * View helper for generating uris
 * @author Lucas Cavalcanti
 * @since 3.4.0
 *
 */
public class LinkToHandler extends ForwardingMap<Class<?>, Object> {

	private static final Logger logger = LoggerFactory.getLogger(LinkToHandler.class);
	
	private final ServletContext context;
	private final Router router;

	public LinkToHandler(ServletContext context, Router router) {
		this.context = context;
		this.router = router;
	}
	
	public void start() {
		logger.info("Registering linkTo component");
		context.setAttribute("linkTo", this);
	}
	
    @Override
    protected Map<Class<?>, Object> delegate() {
        return Collections.emptyMap();
    }

    @Override
    public LinkMethod get(Object key) {
        return new LinkMethod((Class<?>) key);
    }

	class LinkMethod extends ForwardingMap<String, Linker> {

		private final Class<?> controller;
		public LinkMethod(Class<?> controller) {
			this.controller = controller;
		}
		@Override
		protected Map<String, Linker> delegate() {
			return Collections.emptyMap();
		}
		
		@Override
		public Linker get(Object key) {
			return new Linker(controller, key.toString());
		}
		
		
		@Override
		public String toString() {
			throw new IllegalArgumentException("uncomplete linkTo[" + controller.getSimpleName() + "]. You must specify the method.");
		}
	}
	
	class Linker extends ForwardingMap<Object, Linker> {

		private final List<Object> args;
		private final String methodName;
		private final Class<?> controller;

		public Linker(Class<?> controller, String methodName) {
			this(controller, methodName, new ArrayList<Object>());
		}

		public Linker(Class<?> controller, String methodName, List<Object> args) {
			this.controller = controller;
			this.methodName = methodName;
			this.args = args;
		}
		
		@Override
		public Linker get(Object key) {
			List<Object> newArgs = new ArrayList<Object>(args);
			newArgs.add(key);
			return new Linker(controller, methodName, newArgs);
		}

		@Override
		protected Map<Object, Linker> delegate() {
			return Collections.emptyMap();
		}
		
		@Override
		public String toString() {
			Method method = null;
			
			if(getMethodsAmountWithSameName() > 1 && args.size() > 0) {
				method = new Mirror().on(controller).reflect().method(methodName).withArgs(getClasses(args));
			} else {
				method = findMethodWithName(controller, methodName);
			}
			
			if(method == null)
				throw new IllegalArgumentException("There are no methods on " + controller + " named " + methodName);
			
			return context.getContextPath() + router.urlFor(controller, method, args.toArray());
		}

		private Method findMethodWithName(Class<?> type, String name) {
			for (Method method : type.getDeclaredMethods()) {
				if (!method.isBridge() && method.getName().equals(name)) {
					return method;
				}
			}
			
			if (type.getSuperclass().equals(Object.class)) {
				return null;
			}
			
			return findMethodWithName(type.getSuperclass(), name);
		}
		
		private int getMethodsAmountWithSameName() {
			int amount = 0;
			for (Method method : controller.getDeclaredMethods()) {
				if (!method.isBridge() && method.getName().equals(methodName)) {
					amount++;
				}
			}
			
			return amount;
		}
		
		private Class<?>[] getClasses(List<Object> params) {
			Class<?>[] classes = new Class<?>[params.size()];
			for(int i = 0; i < params.size(); i ++) {
				classes[i] = params.get(i).getClass();
			}
			return classes;
	   }
	}
}
