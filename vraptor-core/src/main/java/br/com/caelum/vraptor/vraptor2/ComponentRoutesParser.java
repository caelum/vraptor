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
import br.com.caelum.vraptor.http.route.RoutesParser;
import br.com.caelum.vraptor.http.route.TypeFinder;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceClass;

/**
 * A vraptor 2 and 3 compatible routes creator.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class ComponentRoutesParser implements RoutesParser {

    private final Proxifier proxifier;

	private final RoutesParser delegate;

	private final Logger logger = LoggerFactory.getLogger(ComponentRoutesParser.class);

	private final TypeFinder finder;

    public ComponentRoutesParser(Proxifier proxifier, TypeFinder finder) {
        this.proxifier = proxifier;
		this.finder = finder;
        this.delegate = new PathAnnotationRoutesParser(proxifier, finder);
    }

	public List<Route> rulesFor(ResourceClass resource) {
		List<Route> routes = new ArrayList<Route>();
		Class<?> type = resource.getType();
        if(!Info.isOldComponent(resource)) {
        	return delegate.rulesFor(resource);
        }
        logger.warn("VRaptor2 component found, you should migrate it to VRaptor3: " + type.getName());
		registerRulesFor(type, type, routes);
        parse(type, type);
		return routes;
    }

    private void parse(Class<?> type, Class<?> originalType) {
        if (type.equals(Object.class)) {
            return;
        }
        for (Class<? extends Annotation> annotation : new Class[] { Out.class, In.class, Parameter.class }) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotation)) {
                    logger.error("Field " + field.getName() + " from " + originalType.getName() + " is annotated with "
                            + annotation.getName() + " but is not supported by VRaptor3! Read the migration guide.");
                }
            }
        }
        for (Method method : type.getDeclaredMethods()) {
            for (Class<? extends Annotation> annotation : new Class[] { Out.class, In.class }) {
                if (method.isAnnotationPresent(annotation)) {
                    logger.error("Method " + method.getName() + " from " + originalType.getName()
                            + " is annotated with " + annotation.getName()
                            + " but is not supported by VRaptor3! Read the migration guide.");
                }
            }
            if (method.isAnnotationPresent(Logic.class)) {
                logger.warn("Method " + method.getName() + " from " + originalType.getName()
                        + " is annotated with @Logic. Although its supported, we suggest you to migrate to @Path.");
            }
            if (method.isAnnotationPresent(Validate.class)) {
                Validate validate = method.getAnnotation(Validate.class);
                if (validate.fields().length != 0) {
                    logger.error("Method " + method.getName() + " from " + originalType.getName()
                            + " is annotated with @Validate with fields. This is not supported.");
                }
            }
        }
        parse(type.getSuperclass(), type);
    }


	private void registerRulesFor(Class<?> actualType, Class<?> baseType, List<Route> routes) {
		if (actualType.equals(Object.class)) {
			return;
		}
        for (Method javaMethod : actualType.getDeclaredMethods()) {
			if (!isEligible(javaMethod) || isGetter(javaMethod)) {
                continue;
            }
			String uri = getUriFor(javaMethod, baseType);
			RouteBuilder builder = new RouteBuilder(proxifier, finder, uri);
			for (HttpMethod m : HttpMethod.values()) {
				if (javaMethod.isAnnotationPresent(m.getAnnotation())) {
					builder.with(m);
				}
			}
			builder.is(baseType, javaMethod);
			routes.add(builder.build());
        }
		registerRulesFor(actualType.getSuperclass(), baseType, routes);
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
