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

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.http.ListOfRules;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.TypeCreator;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.Info;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The default implementation of resource localization rules. It also uses a
 * Path annotation to discover path->method mappings using the supplied
 * ResourceAndMethodLookup.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class DefaultRouter implements Router {

    private final Logger logger = LoggerFactory.getLogger(DefaultRouter.class);

    private final List<Rule> routes = new ArrayList<Rule>();
    private final Set<Resource> resources = new HashSet<Resource>();
    private final RoutesParser routesParser;
    private final ParameterNameProvider provider;
    private final TypeCreator creator;
    private final Proxifier proxifier;

    public DefaultRouter(RoutesConfiguration config, RoutesParser routesParser, ParameterNameProvider provider,
            Proxifier proxifier, TypeCreator creator) {
        this.routesParser = routesParser;
        this.provider = provider;
        this.proxifier = proxifier;
        this.creator = creator;
        config.config(this);
    }

    public void add(ListOfRules rulesToAdd) {
        List<Rule> rules = rulesToAdd.getRules();
        add(rules);
    }

    private void add(List<Rule> rules) {
        for (Rule r : rules) {
            add(r);
        }
    }

    /**
     * You can override this method to get notified by all added routes.
     */
    protected void add(Rule r) {
        resources.add(r.getResource());
        this.routes.add(r);
    }

    public ResourceMethod parse(String uri, HttpMethod method, MutableRequest request) {
        for (Rule rule : routes) {
            ResourceMethod value = rule.matches(uri, method, request);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    public Set<Resource> all() {
        return resources;
    }

    public void register(Resource resource) {
        List<Rule> rules = routesParser.rulesFor(resource);
        logger.debug(String.format("registering rules for resource: %s. Rules: %s", resource, rules));
        add(rules);
    }

    public <T> String urlFor(Class<T> type, Method method, Object... params) {
        for (Rule rule : routes) {
            if (rule.getResource().getType().equals(type) && rule.getResourceMethod().getMethod().equals(method)) {
                String[] names = provider.parameterNamesFor(method);
                Class<?> parameterType = creator.typeFor(rule.getResourceMethod());
                try {
                    Object root = parameterType.getConstructor().newInstance();
                    for (int i = 0; i < names.length; i++) {
                        Method setter = findSetter(parameterType, "set" + Info.capitalize(names[i]));
                        setter.invoke(root, params[i]);
                    }
                    return rule.urlFor(root);
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

    public List<Rule> allRoutes() {
        return routes;
    }

    public Proxifier getProxifier() {
        return proxifier;
    }

}
