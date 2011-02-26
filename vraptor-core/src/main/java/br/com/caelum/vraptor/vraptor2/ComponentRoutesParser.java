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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.vidageek.mirror.dsl.Matcher;
import net.vidageek.mirror.dsl.Mirror;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vraptor.annotations.In;
import org.vraptor.annotations.Logic;
import org.vraptor.annotations.Out;
import org.vraptor.annotations.Parameter;
import org.vraptor.plugin.hibernate.Validate;

import br.com.caelum.vraptor.http.route.PathAnnotationRoutesParser;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.http.route.RouteBuilder;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.http.route.RoutesParser;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceClass;

/**
 * A vraptor 2 and 3 compatible routes creator.
 *
 * @author Guilherme Silveira
 */
@Component
@ApplicationScoped
public class ComponentRoutesParser implements RoutesParser {

	private static final Logger logger = LoggerFactory.getLogger(ComponentRoutesParser.class);

	private final RoutesParser delegate;

	private final Router router;


    public ComponentRoutesParser(Router router) {
        this.router = router;
        this.delegate = new PathAnnotationRoutesParser(router);
    }

	public List<Route> rulesFor(ResourceClass resource) {
		List<Route> routes = new ArrayList<Route>();
		Class<?> type = resource.getType();
        if(!Info.isOldComponent(resource)) {
        	return delegate.rulesFor(resource);
        }
        logger.warn("VRaptor2 component found, you should migrate it to VRaptor3: " + type.getName());
		registerRulesFor(type, routes);
        parse(type);
		return routes;
    }

    private void parse(Class<?> type) {
        checkFields(type);
        for (Method method : new Mirror().on(type).reflectAll().methods()) {
            checkOutjections(type, method);
            checkLogic(type, method);
            checkValidate(type, method);
        }
    }

	private void checkValidate(Class<?> type, Method method) {
		if (method.isAnnotationPresent(Validate.class)) {
		    Validate validate = method.getAnnotation(Validate.class);
		    if (validate.fields().length != 0) {
		        logger.error("Method " + method.getName() + " from " + type.getName()
		                + " is annotated with @Validate with fields. This is not supported.");
		    }
		}
	}

	private void checkLogic(Class<?> type, Method method) {
		if (method.isAnnotationPresent(Logic.class)) {
		    logger.warn("Method " + method.getName() + " from " + type.getName()
		            + " is annotated with @Logic. Although its supported, we suggest you to migrate to @Path.");
		}
	}

	private void checkOutjections(Class<?> type, Method method) {
		for (Class<? extends Annotation> annotation : new Class[] { Out.class, In.class }) {
		    if (method.isAnnotationPresent(annotation)) {
		        logger.error("Method " + method.getName() + " from " + type.getName()
		                + " is annotated with " + annotation.getName()
		                + " but is not supported by VRaptor3! Read the migration guide.");
		    }
		}
	}

	private void checkFields(Class<?> type) {
		for (Class<? extends Annotation> annotation : new Class[] { Out.class, In.class, Parameter.class }) {
            for (Field field : new Mirror().on(type).reflectAll().fields()) {
                if (field.isAnnotationPresent(annotation)) {
                    logger.error("Field " + field.getName() + " from " + type.getName() + " is annotated with "
                            + annotation.getName() + " but is not supported by VRaptor3! Read the migration guide.");
                }
            }
        }
	}

	private void registerRulesFor(Class<?> baseType, List<Route> routes) {
		List<Method> methods = new Mirror().on(baseType).reflectAll().methodsMatching(suitableMethod());
		for (Method javaMethod : methods) {
			String uri = getUriFor(javaMethod, baseType);
			RouteBuilder builder = router.builderFor(uri);
			for (HttpMethod m : HttpMethod.values()) {
				if (javaMethod.isAnnotationPresent(m.getAnnotation())) {
					builder.with(m);
				}
			}
			builder.is(baseType, javaMethod);
			routes.add(builder.build());
		}
	}

	private Matcher<Method> suitableMethod() {
		return new Matcher<Method>() {
			public boolean accepts(Method method) {
				return isEligible(method) && !isGetter(method);
			}
		};
	}

	private boolean isGetter(Method javaMethod) {
		return javaMethod.getName().startsWith("get") || javaMethod.getName().startsWith("is");
	}

	private String getUriFor(Method javaMethod, Class<?> type) {
        String componentName = Info.getComponentName(type);
        String logicName = Info.getLogicName(javaMethod);
        return "/" + componentName + "." + logicName + ".logic";
	}

	private boolean isEligible(Method javaMethod) {
		return Modifier.isPublic(javaMethod.getModifiers()) && !Modifier.isStatic(javaMethod.getModifiers());
	}

}
