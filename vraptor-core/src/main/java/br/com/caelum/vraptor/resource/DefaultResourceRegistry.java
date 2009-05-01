/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
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
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.resource;

import java.util.ArrayList;
import java.util.List;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.ioc.ApplicationScoped;

/**
 * This default registry uses a Path annotation to discover path->method
 * mappings using the supplied ResourceAndMethodLookup.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class DefaultResourceRegistry implements ResourceRegistry {

    private final List<ResourceAndMethodLookup> lookup = new ArrayList<ResourceAndMethodLookup>();
    private final List<Resource> resources = new ArrayList<Resource>();
    private final MethodLookupBuilder lookupBuilder;

    public DefaultResourceRegistry(MethodLookupBuilder lookupBuilder) {
        this.lookupBuilder = lookupBuilder;
        register(new DefaultResource(VRaptorInfo.class));
    }

    public void register(Resource... resources) {
        for (Resource resource : resources) {
            this.lookup.add(lookupBuilder.lookupFor(resource));
            this.resources.add(resource);
        }
    }

    public ResourceMethod parse(String id, HttpMethod methodName, MutableRequest request) {
        for (ResourceAndMethodLookup lookuper : lookup) {
            ResourceMethod method = lookuper.methodFor(id, methodName);
            if (method != null) {
                return method;
            }
        }
        return null;
    }

    public List<Resource> all() {
        return this.resources;
    }

}
