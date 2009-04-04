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
package br.com.caelum.vraptor.ioc.pico;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;

import br.com.caelum.vraptor.converter.CachedConverters;
import br.com.caelum.vraptor.core.DefaultConverters;
import br.com.caelum.vraptor.core.DefaultInterceptorStack;
import br.com.caelum.vraptor.core.DefaultRequestExecution;
import br.com.caelum.vraptor.core.DefaultResult;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.http.OgnlParametersProvider;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.InstantiateInterceptor;
import br.com.caelum.vraptor.interceptor.ResourceLookupInterceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.resource.Resource;
import br.com.caelum.vraptor.resource.ResourceRegistry;
import br.com.caelum.vraptor.view.jsp.PageResult;

public class PicoBasedContainer implements Container {

    private final MutablePicoContainer container;

    public PicoBasedContainer(MutablePicoContainer parent, VRaptorRequest request, ResourceRegistry resources) {
        this.container = new PicoBuilder(parent).withCaching().build();
        // TODO try to remove addComponent(this) - InstantiateInterceptor and InterceptorStack
        // needs to instantiate objects with dependency injection
        this.container.addComponent(this);
        this.container.addComponent(request).addComponent(request.getRequest()).addComponent(request.getResponse());
        this.container.addComponent(DefaultInterceptorStack.class).addComponent(DefaultRequestExecution.class);
        this.container.addComponent(ResourceLookupInterceptor.class).addComponent(InstantiateInterceptor.class);
        this.container.addComponent(DefaultResult.class).addComponent(ExecuteMethodInterceptor.class);
        this.container.addComponent(PageResult.class);
        this.container.addComponent(OgnlParametersProvider.class);
        this.container.addComponent(new CachedConverters(new DefaultConverters()));
        for (Resource resource : resources.all()) {
            this.container.addComponent(resource.getType());
        }
    }

    public <T> T instanceFor(Class<T> type) {
        return container.getComponent(type);
    }

    public void register(Object instance) {
        this.container.addComponent(instance);
    }

    public <T> void register(Class<T> type) {
        this.container.addComponent(type);
    }

}
