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

package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceClass;

/**
 * The default parser routes creator uses the path annotation to create rules.
 * Note that methods are only registered to be public accessible if the type is
 * annotated with @Resource.
 *
 * If you want to override the convention for default URI, you can create a
 * class like:
 *
 * public class MyRoutesParser extends PathAnnotationRoutesParser { //delegate
 * constructor protected String extractControllerNameFrom(Class<?> type) {
 * return //your convention here }
 *
 * protected String defaultUriFor(String controllerName, String methodName) {
 * return //your convention here } }
 *
 * @author Guilherme Silveira
 * @author Lucas Cavalcanti
 */
@ApplicationScoped
public class PathAnnotationRoutesParser implements RoutesParser {

	private static final Logger logger = LoggerFactory.getLogger(PathAnnotationRoutesParser.class);

	private final Proxifier proxifier;
	private final TypeFinder finder;

	public PathAnnotationRoutesParser(Proxifier proxifier, TypeFinder finder) {
		this.proxifier = proxifier;
		this.finder = finder;
	}

	public List<Route> rulesFor(ResourceClass resource) {
		List<Route> routes = new ArrayList<Route>();
		Class<?> baseType = resource.getType();
		registerRulesFor(baseType, baseType, routes);
		return routes;
	}

	private void registerRulesFor(Class<?> actualType, Class<?> baseType, List<Route> routes) {
		if (actualType.equals(Object.class)) {
			return;
		}
		for (Method javaMethod : actualType.getDeclaredMethods()) {
			if (isEligible(javaMethod)) {
				String[] uris = getURIsFor(javaMethod, baseType);

				for (String uri : uris) {
					RouteBuilder rule = new RouteBuilder(proxifier, finder, uri);
					for (HttpMethod m : HttpMethod.values()) {
						if (javaMethod.isAnnotationPresent(m.getAnnotation())) {
							rule.with(m);
						}
					}
					if (javaMethod.isAnnotationPresent(Path.class)) {
						rule.withPriority(javaMethod.getAnnotation(Path.class).priority());
					}
					rule.is(baseType, javaMethod);
					routes.add(rule.build());
				}
			}
		}
		registerRulesFor(actualType.getSuperclass(), baseType, routes);
	}

	private boolean isEligible(Method javaMethod) {
		return Modifier.isPublic(javaMethod.getModifiers()) && !Modifier.isStatic(javaMethod.getModifiers());
	}

	private String[] getURIsFor(Method javaMethod, Class<?> type) {
		if (javaMethod.isAnnotationPresent(Path.class)) {
			String[] uris = javaMethod.getAnnotation(Path.class).value();

			if (uris.length == 0)
				throw new IllegalArgumentException("You must specify at least one path in @Path at " + javaMethod);

			for (int i = 0; i < uris.length; i++) {
				String uri = uris[i];
				if (!uri.startsWith("/")) {
					logger.warn("All uris from @Path must start with a '/'. Please change it on " + javaMethod);
					uris[i] = "/" + uri;
				}
			}
			return uris;
		}
		return new String[] { defaultUriFor(extractControllerNameFrom(type), javaMethod.getName()) };
	}

	/**
	 * You can override this method for use a different convention for your
	 * controller name, given a type
	 */
	protected String extractControllerNameFrom(Class<?> type) {
		String baseName = lowerFirstCharacter(type.getSimpleName());
		if (baseName.endsWith("Controller")) {
			return "/" + baseName.substring(0, baseName.lastIndexOf("Controller"));
		}
		return "/" + baseName;
	}

	/**
	 * You can override this method for use a different convention for your
	 * default URI, given a controller name and a method name
	 */
	protected String defaultUriFor(String controllerName, String methodName) {
		return controllerName + "/" + methodName;
	}

	private String lowerFirstCharacter(String baseName) {
		return baseName.toLowerCase().substring(0, 1) + baseName.substring(1, baseName.length());
	}

}
