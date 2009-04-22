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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Interceptor;
import br.com.caelum.vraptor.RegisterContainer;
import br.com.caelum.vraptor.core.VRaptorRequest;
import br.com.caelum.vraptor.interceptor.InterceptorRegistry;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.SessionScoped;
import br.com.caelum.vraptor.resource.ResourceRegistry;

/**
 * Provides containers, controlling all scopes and registering all different
 * components on their respective areas.
 * 
 * @author Guilherme Silveira
 */
public class PicoContainersProvider implements RegisterContainer {

    public static final String CONTAINER_SESSION_KEY = PicoContainersProvider.class.getName() + ".session";

    private static final Logger logger = LoggerFactory.getLogger(PicoContainersProvider.class);

    private final List<Class<?>> applicationScoped = new ArrayList<Class<?>>();
    private final List<Class<?>> sessionScoped = new ArrayList<Class<?>>();
    private final List<Class<?>> requestScoped = new ArrayList<Class<?>>();
    private final MutablePicoContainer container;

    public PicoContainersProvider(MutablePicoContainer container) {
        this.container = container;
    }

    public void register(Class<?> type) {
        for (Class<?> interfaceType : type.getInterfaces()) {
            if (alreadyRegistered(interfaceType)) {
                logger.debug("Overriding interface " + interfaceType.getName() + " with " + type.getName());
            }
        }
        if (type.isAnnotationPresent(ApplicationScoped.class)) {
            logger.debug("Registering " + type.getName() + " as an application component");
            this.applicationScoped.add(type);
        } else if (type.isAnnotationPresent(SessionScoped.class)) {
            logger.debug("Registering " + type.getName() + " a an session component");
            this.sessionScoped.add(type);
        } else {
            logger.debug("Registering " + type.getName() + " as a request component");
            this.requestScoped.add(type);
        }
    }
    
    @SuppressWarnings("unused") 
    private boolean alreadyRegistered(Class<?> interfaceType) {
        for (List<Class<?>> scopes : new List[] { applicationScoped, sessionScoped, requestScoped }) {
            for (Class<?> type : applicationScoped) {
                if (interfaceType.isAssignableFrom(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Container provide(VRaptorRequest request) {
        HttpSession session = request.getRequest().getSession();
        MutablePicoContainer sessionScope = (MutablePicoContainer) session.getAttribute(CONTAINER_SESSION_KEY);
        if (sessionScope == null) {
            sessionScope = createSessionContainer(session);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Request components are " + requestScoped);
        }
        MutablePicoContainer requestScope = new PicoBuilder(sessionScope).withCaching().build();
        // TODO guarantee request component registered order on tests!!!
        for (Class<?> componentType : requestScoped) {
            requestScope.addComponent(componentType);
        }
        for (Class<? extends Interceptor> type : this.container.getComponent(InterceptorRegistry.class).all()) {
            requestScope.addComponent(type);
        }
        requestScope.addComponent(request).addComponent(request.getRequest()).addComponent(request.getResponse());
        // cache(CachedConverters.class, Converters.class);
        PicoBasedContainer baseContainer = new PicoBasedContainer(requestScope, request, this.container
                .getComponent(ResourceRegistry.class));
        return baseContainer;
    }

    private MutablePicoContainer createSessionContainer(HttpSession session) {
        MutablePicoContainer sessionScope = new PicoBuilder(this.container).withCaching().build();
        sessionScope.addComponent(HttpSession.class, session);
        session.setAttribute(CONTAINER_SESSION_KEY, sessionScope);
        if (logger.isDebugEnabled()) {
            logger.debug("Session components are " + sessionScoped);
        }
        for (Class<?> componentType : sessionScoped) {
            sessionScope.addComponent(componentType);
        }
        return sessionScope;
    }

    /**
     * Registers all application scoped elements into the container.
     */
    public void init() {
        for (Class<?> type : applicationScoped) {
            logger.debug("Initializing application scope with " + type);
            this.container.addComponent(type);
        }
    }

}
