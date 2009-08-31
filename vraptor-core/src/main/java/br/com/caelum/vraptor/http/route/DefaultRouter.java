/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.http.route;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.VRaptorInfo;
import br.com.caelum.vraptor.util.collections.Filters;

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
	private final RoutesParser routesParser;
	private final TypeCreator creator;

	public DefaultRouter(RoutesConfiguration config, RoutesParser resourceRoutesCreator,
			Proxifier proxifier, TypeCreator creator) {
		this.routesParser = resourceRoutesCreator;
		this.creator = creator;
		this.proxifier = proxifier;
		// this resource should be kept here so it doesnt matter whether
		// the user uses a custom routes config
		RouteBuilder rule = new RouteBuilder(proxifier, "/is_using_vraptor");
		try {
			rule.is(VRaptorInfo.class).info();
			add(rule.build());
		} catch (IOException e) {
			// ignorable
		}
		config.config(this);
	}

	private void add(List<Route> rules) {
		for (Route r : rules) {
			add(r);
		}
	}

	public Proxifier getProxifier() {
		return proxifier;
	}

	/**
	 * You can override this method to get notified by all added routes.
	 */
	public void add(Route r) {
		this.routes.add(r);
	}

	public ResourceMethod parse(String uri, HttpMethod method, MutableRequest request) {
		Iterator<Route> matches = Iterators.filter(routes.iterator(), Filters.canHandle(uri, method));
		if (matches.hasNext()) {
			Route route = matches.next();
			checkIfThereIsAnotherRoute(uri, method, matches, route);
			return route.matches(uri, method, request);
		}
		return null;
	}

	private void checkIfThereIsAnotherRoute(String uri, HttpMethod method,
			Iterator<Route> matches, Route route) {
		if (matches.hasNext()) {
			Route otherRoute = matches.next();
			if (otherRoute.getPriority() == route.getPriority()) {
				throw new VRaptorException(
						MessageFormat.format("There are two rules that matches the uri '{0}' with method {1}: {2} with same priority." +
								" Consider using @Path priority attribute.",
								uri, method, Arrays.asList(route, otherRoute)));
			}
		}
	}

	public void register(ResourceClass resource) {
		add(this.routesParser.rulesFor(resource));
	}

	public <T> String urlFor(Class<T> type, Method method, Object... params) {
		Iterator<Route> matches = Iterators.filter(routes.iterator(), Filters.canHandle(type, method));
		if (matches.hasNext()) {
			try {
				ResourceMethod resourceMethod = DefaultResourceMethod.instanceFor(type, method);
				return matches.next().urlFor(type, method, creator.instanceWithParameters(resourceMethod, params));
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
