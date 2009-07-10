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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.ResourceClass;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.VRaptorInfo;
import br.com.caelum.vraptor.vraptor2.Info;

/**
 * The default implementation of resource localization rules. It also uses a
 * Path annotation to discover path->method mappings using the supplied
 * ResourceAndMethodLookup.
 *
 * When parsing a uri, if no route matches this uri, we try again inserting
 * or removing a / of the end of this uri:
 * uri: /resource
 * route: /resource/
 * will match, and vice versa.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class DefaultRouter implements Router {

	private final Proxifier proxifier;
	private final List<Route> routes = new ArrayList<Route>();
	private final Set<ResourceClass> resources = new HashSet<ResourceClass>();
	private final RoutesParser routesParser;
	private final ParameterNameProvider provider;
	private final TypeCreator creator;

	public DefaultRouter(RoutesConfiguration config, RoutesParser resourceRoutesCreator,
			ParameterNameProvider provider, Proxifier proxifier, TypeCreator creator) {
		this.routesParser = resourceRoutesCreator;
		this.provider = provider;
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
		ResourceClass resource = r.getResource();
		if (resource != null) {
			this.resources.add(resource);
		}
		this.routes.add(r);
	}

	/**
	 *  When parsing a uri, if no route matches this uri, we try again inserting
	 * or removing a / of the end of this uri:
	 * uri: /resource
	 * route: /resource/
	 * will match, and vice versa.
	 */
	public ResourceMethod parse(String uri, HttpMethod method, MutableRequest request) {
		ResourceMethod resourceMethod = parcialParse(uri, method, request);
		if (resourceMethod == null) {
			return parcialParse(toggleSlash(uri), method, request);
		}
		return resourceMethod;
	}

	private String toggleSlash(String uri) {
		if (uri.endsWith("/")) {
			return uri.substring(0, uri.length() - 1);
		} else {
			return uri + "/";
		}
	}

	private ResourceMethod parcialParse(String uri, HttpMethod method, MutableRequest request) {
		for (Route rule : routes) {
			ResourceMethod value = rule.matches(uri, method, request);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	public Set<ResourceClass> allResources() {
		// TODO: defensive copy? (collections.unmodifiable)
		return resources;
	}

	public void register(ResourceClass resource) {
		add(this.routesParser.rulesFor(resource));
	}

	public <T> String urlFor(Class<T> type, Method method, Object... params) {
		for (Route route : routes) {
			if (route.canHandle(type, method)) {
				String[] names = provider.parameterNamesFor(method);
				Class<?> parameterType = creator.typeFor(new DefaultResourceMethod(new DefaultResourceClass(type),
						method));
				try {
					Object root = parameterType.getConstructor().newInstance();
					for (int i = 0; i < names.length; i++) {
						Method setter = findSetter(parameterType, "set" + Info.capitalize(names[i]));
						setter.invoke(root, params[i]);
					}
					return route.urlFor(type, method, root);
				} catch (Exception e) {
					throw new VRaptorException("The selected route is invalid for redirection: " + type.getName() + "."
							+ method.getName(), e);
				}
			}
		}
		throw new RouteNotFoundException("The selected route is invalid for redirection: " + type.getName() + "."
				+ method.getName());
	}

	private Method findSetter(Class<?> parameterType, String methodName) {
		for (Method m : parameterType.getDeclaredMethods()) {
			if (m.getName().equals(methodName)) {
				return m;
			}
		}
		throw new VRaptorException(
				"Unable to redirect using route as setter method for parameter setting was not created. "
						+ "Thats probably a bug on your type creator. "
						+ "If you are using the default type creator, notify VRaptor.");
	}

	public List<Route> allRoutes() {
		return routes;
	}

}
