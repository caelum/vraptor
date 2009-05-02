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

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.http.ListOfRules;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.resource.ResourceParserRoutesCreator;
import br.com.caelum.vraptor.resource.VRaptorInfo;

/**
 * The default implementation of resource localization rules.
 * It also uses a Path annotation to discover path->method
 * mappings using the supplied ResourceAndMethodLookup.
 * 
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class DefaultRouter implements Router {

	private final List<Rule> rules = new ArrayList<Rule>();
    private final Set<Resource> resources = new HashSet<Resource>();
	private final ResourceParserRoutesCreator resourceRoutesCreator;

    public DefaultRouter(RoutesConfiguration config, ResourceParserRoutesCreator resourceRoutesCreator) {
        this.resourceRoutesCreator = resourceRoutesCreator;
		// this resource should be kept here so it doesnt matter whether
        // the user uses a custom routes config
    	UriBasedRoute rule = new UriBasedRoute("/is_using_vraptor");
    	try {
			rule.is(VRaptorInfo.class).info();
		} catch (IOException e) {
			// ignorable
		}
    	add(rule);
        config.config(this);
    }

	public void add(ListOfRules rulesToAdd) {
		List<Rule> rules = rulesToAdd.getRules();
		add(rules);
	}

	private void add(List<Rule> rules) {
		for(Rule r : rules) {
			add(r);
		}
	}

	private void add(Rule r) {
		resources.add(r.getResource());
		this.rules.add(r);
	}

	public ResourceMethod parse(String uri, HttpMethod method, MutableRequest request) {
		for (Rule rule : rules) {
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
		add(this.resourceRoutesCreator.rulesFor(resource));
	}

	public <T> String urlFor(Class<T> type, Method method, Object... params) {
        Path path = method.getAnnotation(Path.class);
        if (path != null) {
            String value = path.value();
            value = value.replaceAll("\\*", "");
            return value;
        }
        return "/" + type.getSimpleName() + "/" + method.getName();
	}

}
