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

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import br.com.caelum.vraptor.Delete;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
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
 * constructor protected String extractControllerNameFrom(Class&lt;?&gt; type) {
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

	private final Router router;

	public PathAnnotationRoutesParser(Router router) {
		this.router = router;
	}

	public List<Route> rulesFor(ResourceClass resource) {
		Class<?> baseType = resource.getType();
		return registerRulesFor(baseType);
	}

	protected List<Route> registerRulesFor(Class<?> baseType) {
		EnumSet<HttpMethod> typeMethods = getHttpMethods(baseType);

		List<Route> routes = new ArrayList<Route>();
		for (Method javaMethod : baseType.getMethods()) {
			if (isEligible(javaMethod)) {
				String[] uris = getURIsFor(javaMethod, baseType);

				for (String uri : uris) {
					RouteBuilder rule = router.builderFor(uri);

					EnumSet<HttpMethod> methods = getHttpMethods(javaMethod);

					rule.with(methods.isEmpty() ? typeMethods : methods);

					Integer priority = getPriority(javaMethod);
					
					if(priority != null){
						rule.withPriority(priority);
					}

					rule.is(baseType, javaMethod);
					routes.add(rule.build());
				}
			}
		}
		
		return routes;
	}
	
	private Integer getPriority(Method method){
		if (method.isAnnotationPresent(Path.class)) {
			return method.getAnnotation(Path.class).priority();
		}else if (method.isAnnotationPresent(Get.class)) {
			return method.getAnnotation(Get.class).priority();
		}else if (method.isAnnotationPresent(Post.class)) {
			return method.getAnnotation(Post.class).priority();
		}else if (method.isAnnotationPresent(Put.class)) {
			return method.getAnnotation(Put.class).priority();
		}else if (method.isAnnotationPresent(Delete.class)) {
			return method.getAnnotation(Delete.class).priority();
		}
		
		return null;
	}

	private EnumSet<HttpMethod> getHttpMethods(AnnotatedElement annotated) {
		EnumSet<HttpMethod> methods = EnumSet.noneOf(HttpMethod.class);
		for (HttpMethod method : HttpMethod.values()) {
			if (annotated.isAnnotationPresent(method.getAnnotation())) {
				methods.add(method);
			}
		}
		return methods;
	}

	protected boolean isEligible(Method javaMethod) {
		return Modifier.isPublic(javaMethod.getModifiers())
			&& !Modifier.isStatic(javaMethod.getModifiers())
			&& !javaMethod.isBridge()
			&& !javaMethod.getDeclaringClass().equals(Object.class);
	}

	protected String[] getURIsFor(Method javaMethod, Class<?> type) {

		if (javaMethod.isAnnotationPresent(Path.class)) {
			String[] uris = javaMethod.getAnnotation(Path.class).value();

			if (uris.length == 0) {
				throw new IllegalArgumentException("You must specify at least one path on @Path at " + javaMethod);
			}

			fixURIs(type, uris);

			return uris;
		}else if(javaMethod.getAnnotations().length > 0) {
			String[] uris = getUris(javaMethod);
			
			if(uris.length > 0){
				fixURIs(type, uris);
				return uris;
			}
		}

		return new String[] { defaultUriFor(extractControllerNameFrom(type), javaMethod.getName()) };
	}
	
	private String[] getUris(Method javaMethod){
		
		String[] uris = null;
		
		if (javaMethod.isAnnotationPresent(Get.class)) {
			uris = javaMethod.getAnnotation(Get.class).value();
		}else if (javaMethod.isAnnotationPresent(Post.class)){
			uris = javaMethod.getAnnotation(Post.class).value();
		}else if (javaMethod.isAnnotationPresent(Put.class)) {
			uris = javaMethod.getAnnotation(Put.class).value();
		}else if (javaMethod.isAnnotationPresent(Delete.class)) {
			uris =  javaMethod.getAnnotation(Delete.class).value();
		}
		
		if(uris != null && uris.length > 0 && !uris[0].isEmpty()){
			return uris;
		}
		
		return new String[]{};
	}

	protected void fixURIs(Class<?> type, String[] uris) {
		String prefix = extractPrefix(type);
		for (int i = 0; i < uris.length; i++) {
			if ("".equals(prefix)) {
				uris[i] = fixLeadingSlash(uris[i]);
			} else if ("".equals(uris[i])) {
				uris[i] = prefix;
			} else {
				uris[i] = removeTrailingSlash(prefix) + fixLeadingSlash(uris[i]);
			}
		}
	}

	protected String removeTrailingSlash(String prefix) {
		return prefix.replaceFirst("/$", "");
	}

	protected String extractPrefix(Class<?> type) {
		if (type.isAnnotationPresent(Path.class)) {
			String[] uris = type.getAnnotation(Path.class).value();
			if (uris.length != 1) {
				throw new IllegalArgumentException("You must specify exactly one path on @Path at " + type);
			}
			return fixLeadingSlash(uris[0]);
		} else {
			return "";
		}
	}

	private String fixLeadingSlash(String uri) {
		if (!uri.startsWith("/")) {
			return  "/" + uri;
		}
		return uri;
	}

	/**
	 * You can override this method for use a different convention for your
	 * controller name, given a type
	 */
	protected String extractControllerNameFrom(Class<?> type) {
		String prefix = extractPrefix(type);
		if ("".equals(prefix)) {
			String baseName = lowerFirstCharacter(type.getSimpleName());
			if (baseName.endsWith("Controller")) {
				return "/" + baseName.substring(0, baseName.lastIndexOf("Controller"));
			}
			return "/" + baseName;
		} else {
			return prefix;
		}
	}

	/**
	 * You can override this method for use a different convention for your
	 * default URI, given a controller name and a method name
	 */
	protected String defaultUriFor(String controllerName, String methodName) {
		return controllerName + "/" + methodName;
	}

	protected String lowerFirstCharacter(String baseName) {
		return baseName.toLowerCase().substring(0, 1) + baseName.substring(1, baseName.length());
	}

}
