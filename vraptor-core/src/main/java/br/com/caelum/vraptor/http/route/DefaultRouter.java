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
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.collections.Filters;

import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;

/**
 * The default implementation of resource localization rules. It also uses a
 * Path annotation to discover path->method mappings using the supplied
 * ResourceAndMethodLookup.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class DefaultRouter implements Router {

	private final Proxifier proxifier;
	private final Collection<Route> routes = new PriorityRoutesList();
	private final TypeFinder finder;
	private final Converters converters;
	private final ParameterNameProvider nameProvider;
    private final Evaluator evaluator;

    public DefaultRouter(RoutesConfiguration config,
			Proxifier proxifier, TypeFinder finder, Converters converters, ParameterNameProvider nameProvider, Evaluator evaluator) {
		this.proxifier = proxifier;
		this.finder = finder;
		this.converters = converters;
		this.nameProvider = nameProvider;
        this.evaluator = evaluator;
		config.config(this);
	}

	public RouteBuilder builderFor(String uri) {
		return new DefaultRouteBuilder(proxifier, finder, converters, nameProvider, evaluator, uri);
	}

	/**
	 * You can override this method to get notified by all added routes.
	 */
	public void add(Route r) {
		this.routes.add(r);
	}

	public ResourceMethod parse(String uri, HttpMethod method, MutableRequest request)
						throws MethodNotAllowedException{
		Collection<Route> routesMatchingUriAndMethod = routesMatchingUriAndMethod(uri, method);

		Iterator<Route> iterator = routesMatchingUriAndMethod.iterator();

		Route route = iterator.next();
		checkIfThereIsAnotherRoute(uri, method, iterator, route);

		return route.resourceMethod(request, uri);
	}

	private void checkIfThereIsAnotherRoute(String uri, HttpMethod method, Iterator<Route> iterator, Route route) {
		if (iterator.hasNext()) {
			Route otherRoute = iterator.next();
			if (route.getPriority() == otherRoute.getPriority()) {
				throw new IllegalStateException(
						MessageFormat.format("There are two rules that matches the uri ''{0}'' with method {1}: {2} with same priority." +
								" Consider using @Path priority attribute.",
								uri, method, Arrays.asList(route, otherRoute)));
			}
		}
	}


	private Collection<Route> routesMatchingUriAndMethod(String uri, HttpMethod method) {
		Collection<Route> routesMatchingMethod = Collections2.filter(routesMatchingUri(uri), Filters.allow(method));
		if (routesMatchingMethod.isEmpty()) {
			EnumSet<HttpMethod> allowed = allowedMethodsFor(uri);
			throw new MethodNotAllowedException(allowed, method.toString());
		}
		return routesMatchingMethod;
	}

	public EnumSet<HttpMethod> allowedMethodsFor(String uri) {
		EnumSet<HttpMethod> allowed = EnumSet.noneOf(HttpMethod.class);
		for (Route route : routesMatchingUri(uri)) {
			allowed.addAll(route.allowedMethods());
		}
		return allowed;
	}

	private Collection<Route> routesMatchingUri(String uri) {
		Collection<Route> routesMatchingURI = Collections2.filter(routes, Filters.canHandle(uri));
		if (routesMatchingURI.isEmpty()) {
			throw new ResourceNotFoundException();
		}
		return routesMatchingURI;
	}

	public <T> String urlFor(Class<T> type, Method method, Object... params) {
		Iterator<Route> matches = Iterators.filter(routes.iterator(), Filters.canHandle(type, method));
		if (matches.hasNext()) {
			try {
				return matches.next().urlFor(type, method, params);
			} catch (Exception e) {
				throw new VRaptorException("The selected route is invalid for redirection: " + type.getName() + "."
						+ method.getName(), e);
			}
		}
		throw new RouteNotFoundException("The selected route is invalid for redirection: " + type.getName() + "."
				+ method.getName());
	}

	public List<Route> allRoutes() {
		return Collections.unmodifiableList(new ArrayList<Route>(routes));
	}

}
