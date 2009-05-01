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
package br.com.caelum.vraptor.http;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.resource.DefaultResource;
import br.com.caelum.vraptor.resource.HttpMethod;
import br.com.caelum.vraptor.resource.MethodLookupBuilder;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceAndMethodLookup;
import br.com.caelum.vraptor.resource.ResourceMethod;
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

    private final List<ResourceAndMethodLookup> lookup = new ArrayList<ResourceAndMethodLookup>();
	private final List<Rule> rules = new ArrayList<Rule>();
    private final Set<Resource> resources = new HashSet<Resource>();
    private final MethodLookupBuilder lookupBuilder;

    public DefaultRouter(MethodLookupBuilder lookupBuilder) {
        this.lookupBuilder = lookupBuilder;
        register(new DefaultResource(VRaptorInfo.class));
    }

	public void add(ListOfRules rulesToAdd) {
		List<Rule> rules = rulesToAdd.getRules();
		for(Rule r : rules) {
			resources.add(r.getResource());
			this.rules.add(r);
		}
	}

	public ResourceMethod parse(String uri, HttpMethod method, MutableRequest request) {
		for (Rule rule : rules) {
			ResourceMethod value = rule.matches(uri, method, request);
			if (value != null) {
				return value;
			}
		}
        for (ResourceAndMethodLookup lookuper : lookup) {
            ResourceMethod value = lookuper.methodFor(uri, method);
            if (value != null) {
                return value;
            }
        }
        return null;
	}

	public Set<Resource> all() {
		return resources;
	}

	public void register(Resource... resources) {
        for (Resource resource : resources) {
            this.lookup.add(lookupBuilder.lookupFor(resource));
            this.resources.add(resource);
        }
	}

}
