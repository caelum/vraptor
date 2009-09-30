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
package br.com.caelum.vraptor.vraptor2.outject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.vraptor2.BeanHelper;
import br.com.caelum.vraptor.vraptor2.ComponentInfoProvider;

/**
 * Uses the designated outjecter to outjects all vraptor2 compatible get
 * accessible parameters.<br>
 * VRaptor3-2 compatibility mode only supports type methods, not @Out annotation
 * (further support might be implemented if required).
 * 
 * @author Guilherme Silveira
 */
public class OutjectionInterceptor implements Interceptor {

    private static final String GET = "get";
    private static final String IS = "is";

    private static final Logger logger = LoggerFactory.getLogger(OutjectionInterceptor.class);
    private static final BeanHelper helper = new BeanHelper();
    private final Outjecter outjecter;

    public OutjectionInterceptor(ComponentInfoProvider provider) {
        this.outjecter = provider.getOutjecter();
    }

    public boolean accepts(ResourceMethod method) {
        return true;
    }

    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
        Class<?> type = method.getResource().getType();
        outject(resourceInstance, type);
        stack.next(method, resourceInstance);
    }

    public void outject(Object resourceInstance, Class<?> type) throws InterceptionException {
        Method[] methods = type.getDeclaredMethods();
        for (Method outject : methods) {
            if (Modifier.isPublic(outject.getModifiers()) &&  outject.getName().length() < 3
                    || !(outject.getName().startsWith(IS) || outject.getName().startsWith(GET))) {
                continue;
            }
            if (outject.getParameterTypes().length != 0) {
                logger.error("A get method was found at " + type
                        + " but was not used because it receives parameters. Fix it.");
                continue;
            } else if (outject.getReturnType().equals(void.class)) {
                logger
                        .error("A get method was found at " + type
                                + " but was not used because it returns void. Fix it.");
                continue;
            }
            try {
                Object result = outject.invoke(resourceInstance);
                String name = helper.nameForGetter(outject);
                logger.debug("Outjecting " + name);
                outjecter.include(name, result);
            } catch (IllegalArgumentException e) {
                throw new InterceptionException("Unable to outject value for " + outject.getName(), e);
            } catch (IllegalAccessException e) {
                throw new InterceptionException("Unable to outject value for " + outject.getName(), e);
            } catch (InvocationTargetException e) {
                throw new InterceptionException("Unable to outject value for " + outject.getName(), e.getCause());
            }
        }
    }

}
